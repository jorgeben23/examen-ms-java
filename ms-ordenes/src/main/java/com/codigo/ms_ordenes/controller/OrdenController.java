package com.codigo.ms_ordenes.controller;

import com.codigo.ms_ordenes.Services.OrdenService;
import com.codigo.ms_ordenes.aggregates.request.OrdenRequest;
import com.codigo.ms_ordenes.aggregates.response.OrdenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
@Slf4j
public class OrdenController {

    private final OrdenService ordenService;

    @GetMapping("/saludar")
    public ResponseEntity<String> saludar() {
        return ResponseEntity.ok("Microservicio de órdenes funcionando correctamente");
    }

    @PostMapping
    public ResponseEntity<?> crearOrden(@Valid @RequestBody OrdenRequest request,
                                        HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token no proporcionado"));
            }

            if (!ordenService.validarAccesoUsuario(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Acceso denegado. Solo usuarios pueden crear órdenes"));
            }

            OrdenResponse response = ordenService.crearOrden(request, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creando orden: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping
    public ResponseEntity<?> listarOrdenes(HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token no proporcionado"));
            }

            if (!ordenService.validarAccesoAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Acceso denegado. Se requiere rol ADMIN o SUPERADMIN"));
            }

            List<OrdenResponse> ordenes = ordenService.listarOrdenes();
            return ResponseEntity.ok(ordenes);

        } catch (Exception e) {
            log.error("Error listando órdenes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/mis-ordenes")
    public ResponseEntity<?> listarMisOrdenes(HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token no proporcionado"));
            }

            var usuario = ordenService.validarToken(token);
            if (!usuario.getValid()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido"));
            }

            List<OrdenResponse> ordenes = ordenService.listarOrdenesPorUsuario(usuario.getId());
            return ResponseEntity.ok(ordenes);

        } catch (Exception e) {
            log.error("Error listando órdenes del usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

}
