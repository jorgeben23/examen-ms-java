package com.codigo.ms_ordenes.Services;

import com.codigo.ms_ordenes.aggregates.request.OrdenRequest;
import com.codigo.ms_ordenes.aggregates.response.OrdenResponse;
import com.codigo.ms_ordenes.aggregates.response.ProductoResponse;
import com.codigo.ms_ordenes.aggregates.response.UsuarioResponse;
import com.codigo.ms_ordenes.client.AuthFeignClient;
import com.codigo.ms_ordenes.client.PoductosFeignClient;
import com.codigo.ms_ordenes.entity.OrdenEntity;
import com.codigo.ms_ordenes.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final AuthFeignClient authClient;
    private final PoductosFeignClient productosClient;


    public UsuarioResponse validarToken(String token) {
        try {
            Map<String, Object> respuesta = authClient.validateToken(token);
            log.info("Esta es la respuesta: {}", respuesta);

            Boolean valid = (Boolean) respuesta.get("valid");
            Map<String, Object> userMap = (Map<String, Object>) respuesta.get("user");
            List<String> roles = (List<String>) userMap.get("roles");
            String rol = roles != null && !roles.isEmpty() ? roles.get(0) : null;
            Long id = userMap.get("id") != null ? Long.valueOf(userMap.get("id").toString()) : null;

            return UsuarioResponse.builder()
                    .valid(valid)
                    .rol(rol)
                    .id(id)
                    .build();
        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            return UsuarioResponse.builder()
                    .valid(false)
                    .message("Token inválido")
                    .build();
        }
    }

    public boolean validarAccesoAdmin(String token) {
        try {
            UsuarioResponse usuario = validarToken(token);
            return usuario.getValid() &&
                    ("ADMIN".equals(usuario.getRol()) || "SUPERADMIN".equals(usuario.getRol()));
        } catch (Exception e) {
            log.error("Error validando acceso admin: {}", e.getMessage());
            return false;
        }
    }

    public boolean validarAccesoUsuario(String token) {
        try {
            UsuarioResponse usuario = validarToken(token);
            return usuario.getValid() && "USUARIO".equals(usuario.getRol());
        } catch (Exception e) {
            log.error("Error validando acceso usuario: {}", e.getMessage());
            return false;
        }
    }

    public List<ProductoResponse> validarProductosExistentes(List<Long> productosIds, String token) {
        log.info("IDs de productos recibidos: {}", productosIds);
        log.info("Token recibido: {}", token);

        try {
            List<ProductoResponse> productos = productosIds.stream()
                    .map(id -> {
                        try {
                            ProductoResponse producto = productosClient.obtenerProductoPorId(id, token);
                            log.info("Producto encontrado: ID={}, Nombre={}", producto.getId(), producto.getNombre());
                            return producto;
                        } catch (Exception e) {
                            log.warn("No se pudo obtener el producto con ID {}: {}", id, e.getMessage());
                            return null;
                        }
                    })
                    .collect(Collectors.toList());

            // Validar si algún producto es null (no encontrado o error)
            if (productos.contains(null)) {
                throw new RuntimeException("Uno o más productos no existen o no se pudieron validar");
            }

            return productos;

        } catch (Exception e) {
            log.error("Error validando productos existentes: {}", e.getMessage(), e);
            throw new RuntimeException("Error al validar productos existentes");
        }
    }

    @Transactional
    public OrdenResponse crearOrden(OrdenRequest request, String token) {

        UsuarioResponse usuario = validarToken(token);
        log.info("lo que trae el usuario : {} ", usuario);
        if (!usuario.getValid()) {
            throw new RuntimeException("Token inválido");
        }

        if (!"USUARIO".equals(usuario.getRol())) {
            throw new RuntimeException("Solo los usuarios pueden crear órdenes");
        }

        List<ProductoResponse> productos = validarProductosExistentes(request.getProductosIds(), token);
        log.info("productos: {} ", productos);
        if (productos.contains(null)) {
            throw new RuntimeException("Algunos productos no existen");
        }

        // Calcular total
        Double total = productos.stream()
                .mapToDouble(ProductoResponse::getPrecio)
                .sum();

        // Crear orden usando patrón Builder
        OrdenEntity orden = OrdenEntity.builder()
                .usuarioId(usuario.getId())
                .productosIds(request.getProductosIds())
                .fecha(LocalDateTime.now())
                .estado("PENDIENTE")
                .total(total)
                .build();

        orden = ordenRepository.save(orden);

        // Construir respuesta usando patrón Builder
        return OrdenResponse.builder()
                .id(orden.getId())
                .usuarioId(orden.getUsuarioId())
                .productosIds(orden.getProductosIds())
                .fecha(orden.getFecha())
                .estado(orden.getEstado())
                .total(orden.getTotal())
                .productos(productos)
                .mensaje("Orden creada exitosamente")
                .build();
    }

    public List<OrdenResponse> listarOrdenes() {
        List<OrdenEntity> ordenes = ordenRepository.findAllByOrderByFechaDesc();

        return ordenes.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<OrdenResponse> listarOrdenesPorUsuario(Long usuarioId) {
        List<OrdenEntity> ordenes = ordenRepository.findByUsuarioId(usuarioId);

        return ordenes.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    private OrdenResponse convertirAResponse(OrdenEntity orden) {
        return OrdenResponse.builder()
                .id(orden.getId())
                .usuarioId(orden.getUsuarioId())
                .productosIds(orden.getProductosIds())
                .fecha(orden.getFecha())
                .estado(orden.getEstado())
                .total(orden.getTotal())
                .build();
    }

}
