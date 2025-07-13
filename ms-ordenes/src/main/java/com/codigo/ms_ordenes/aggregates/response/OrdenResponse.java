package com.codigo.ms_ordenes.aggregates.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenResponse {

    private Long id;
    private Long usuarioId;
    private List<Long> productosIds;
    private LocalDateTime fecha;
    private String estado;
    private Double total;
    private List<ProductoResponse> productos;
    private String mensaje;
}
