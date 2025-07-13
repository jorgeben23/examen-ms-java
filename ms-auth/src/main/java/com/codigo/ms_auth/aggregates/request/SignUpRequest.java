package com.codigo.ms_auth.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    private String nombres;
    private String email;
    private String password;
    private String numDoc;
}
