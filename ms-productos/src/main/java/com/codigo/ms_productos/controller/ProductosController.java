package com.codigo.ms_productos.controller;

import com.codigo.ms_productos.Services.ProductoService;
import com.codigo.ms_productos.aggregates.request.ProductoRequest;
import com.codigo.ms_productos.aggregates.response.ProductoResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/productos/")
@RequiredArgsConstructor
@Log4j2
public class ProductosController {

    private final ProductoService productoService;

    @GetMapping("saludar")
    public ResponseEntity<String> listar () {
        return ResponseEntity.ok("Esta es la devolucion");
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> crearProducto(@RequestBody ProductoRequest request,
                                           HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token no proporcionado"));
            }

            if (!productoService.validarAccesoUsuario(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Acceso denegado. Se requiere rol ADMIN o SUPERADMIN"));
            }

            ProductoResponse response = productoService.crearProducto(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error creando producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }


    @GetMapping("/listar")
    public ResponseEntity<?> listarProductos(HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token no proporcionado"));
            }

            if (!productoService.validarAccesoUsuario(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Acceso denegado. Se requiere rol ADMIN o SUPERADMIN"));
            }

            List<ProductoResponse> productos = productoService.listarProductos();
            return ResponseEntity.ok(productos);

        } catch (Exception e) {
            log.error("Error listando productos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id,
                                                @RequestBody ProductoRequest request,
                                                HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token no proporcionado"));
            }

            if (!productoService.validarAccesoUsuario(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Acceso denegado. Se requiere rol ADMIN o SUPERADMIN"));
            }

            ProductoResponse response = productoService.actualizarProducto(id, request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error actualizando producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id,
                                              HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token no proporcionado"));
            }

            if (!productoService.validarAccesoUsuario(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Acceso denegado. Se requiere rol ADMIN o SUPERADMIN"));
            }

            productoService.eliminarProducto(id);
            return ResponseEntity.ok(Map.of("message", "Producto eliminado exitosamente"));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error eliminando producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id,
                                                  HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token no proporcionado"));
            }

            ProductoResponse response = productoService.obtenerProductoPorId(id);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error obteniendo producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }


}
