package com.codigo.ms_productos.Services;

import com.codigo.ms_productos.aggregates.request.ProductoRequest;
import com.codigo.ms_productos.aggregates.response.ProductoResponse;
import com.codigo.ms_productos.client.AuthFeignClient;
import com.codigo.ms_productos.entity.ProductoEntity;
import com.codigo.ms_productos.repository.IProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Log4j2
public class ProductoService {

    private final IProductoRepository productoRepository;
    private final AuthFeignClient authFeignClient;

    public boolean validarAccesoUsuario (String token) {
        try {
            Map<String,Object> respuesta = authFeignClient.validateToken(token);
            boolean valid = (boolean) respuesta.get("valid");
            Map<String, Object> user = (Map<String, Object>) respuesta.get("user");
            List<String> roles = (List<String>) user.get("roles");
            boolean hasAccess = roles.contains("ADMIN") || roles.contains("SUPERADMIN");
            return valid && hasAccess;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ProductoResponse crearProducto(ProductoRequest request) {
        ProductoEntity producto = ProductoEntity.builder()
                .nombre(request.getNombre())
                .precio(request.getPrecio())
                .categoria(request.getCategoria())
                .build();

        ProductoEntity savedProducto = productoRepository.save(producto);

        return new ProductoResponse(
                savedProducto.getId(),
                savedProducto.getNombre(),
                savedProducto.getPrecio(),
                savedProducto.getCategoria()
        );
    }

    public List<ProductoResponse> listarProductos() {
        return productoRepository.findAll().stream()
                .map(producto -> new ProductoResponse(
                        producto.getId(),
                        producto.getNombre(),
                        producto.getPrecio(),
                        producto.getCategoria()
                ))
                .collect(Collectors.toList());
    }

    public ProductoResponse actualizarProducto(Long id, ProductoRequest request) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setCategoria(request.getCategoria());

        ProductoEntity updatedProducto = productoRepository.save(producto);

        return new ProductoResponse(
                updatedProducto.getId(),
                updatedProducto.getNombre(),
                updatedProducto.getPrecio(),
                updatedProducto.getCategoria()
        );
    }

    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    public ProductoResponse obtenerProductoPorId(Long id) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getCategoria()
        );
    }

}
