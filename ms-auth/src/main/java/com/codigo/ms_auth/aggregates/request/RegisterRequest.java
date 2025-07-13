package com.codigo.ms_auth.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private String rol;
    private String numDoc;
}
