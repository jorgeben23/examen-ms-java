package com.codigo.ms_auth.service;

import com.codigo.ms_auth.aggregates.request.RegisterRequest;
import com.codigo.ms_auth.aggregates.request.SignInRequest;
import com.codigo.ms_auth.aggregates.request.SignUpRequest;
import com.codigo.ms_auth.aggregates.response.RegisterResponse;
import com.codigo.ms_auth.aggregates.response.SignInResponse;
import com.codigo.ms_auth.entity.Usuario;

import java.util.List;
import java.util.Map;

public interface AuthenticationService {

    //Registrar Usuario
    Usuario signUpUser(SignUpRequest signUpRequest);
    //Registrar Admin
    Usuario signUpAdmin(SignUpRequest signUpRequest);
    List<Usuario> todos();
    boolean validateToken(String token);
    SignInResponse signIn(SignInRequest signInRequest);
    SignInResponse getTokenByRefreshToken(String token)throws IllegalAccessException;
    RegisterResponse register(RegisterRequest registerRequest);
    Map<String, Object> validateTokenUserInfo(String token);

}
