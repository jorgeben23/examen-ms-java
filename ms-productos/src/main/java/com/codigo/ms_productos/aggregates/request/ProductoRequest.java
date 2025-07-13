package com.codigo.ms_productos.aggregates.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequest {
    private String nombre;
    private Double precio;
    private String categoria;
}
