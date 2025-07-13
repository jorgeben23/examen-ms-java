package com.codigo.ms_ordenes.aggregates.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProductoResponse {

    private Long id;
    private String nombre;
    private Double precio;
    private String categoria;
    private String descripcion;
    private Boolean disponible;
}
