package com.codigo.ms_auth.aggregates.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class SignInResponse {
    private String accessToken;
    private String refreshToken;
}
