package com.codigo.ms_productos.aggregates.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private boolean valid;
    private String message;
    private String nombre;
    private String email;
    private String rol;
}
