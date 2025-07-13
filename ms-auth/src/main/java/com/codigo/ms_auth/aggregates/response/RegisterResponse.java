package com.codigo.ms_auth.aggregates.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterResponse {
    private String message;
    private UserInfo user;

    @Getter
    @Setter
    @Builder
    public  static class UserInfo {
        private Long id;
        private String nombre;
        private String email;
        private String rol;
    }
}
