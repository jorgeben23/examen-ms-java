package com.codigo.ms_auth.aggregates.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {
    private String nombres;
    private String email;
    private String password;
    private String numDoc;
    private List<String> roles;
}
