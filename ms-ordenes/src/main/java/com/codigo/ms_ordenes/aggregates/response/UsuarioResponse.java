package com.codigo.ms_ordenes.aggregates.response;


import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private Boolean valid;
    private String message;
}
