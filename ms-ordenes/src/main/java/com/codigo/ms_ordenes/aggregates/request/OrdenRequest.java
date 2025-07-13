package com.codigo.ms_ordenes.aggregates.request;

import lombok.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrdenRequest {
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotEmpty(message = "Debe incluir al menos un producto")
    private List<Long> productosIds;

    private String observaciones;
}

