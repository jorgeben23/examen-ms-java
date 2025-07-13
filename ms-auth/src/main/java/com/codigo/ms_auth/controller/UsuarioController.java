package com.codigo.ms_auth.controller;

import com.codigo.ms_auth.aggregates.request.RegisterRequest;
import com.codigo.ms_auth.aggregates.request.SignInRequest;
import com.codigo.ms_auth.aggregates.request.SignUpRequest;
import com.codigo.ms_auth.aggregates.request.UsuarioRequest;
import com.codigo.ms_auth.aggregates.response.RegisterResponse;
import com.codigo.ms_auth.aggregates.response.SignInResponse;
import com.codigo.ms_auth.entity.Usuario;
import com.codigo.ms_auth.repository.UsuarioRepository;
import com.codigo.ms_auth.service.AuthenticationService;
import com.codigo.ms_auth.service.JwtService;
import com.codigo.ms_auth.service.UsuarioService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Log4j2
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;


    @PostMapping("/signupuser")
    public ResponseEntity<Usuario> signUpUser(@RequestBody SignUpRequest signUpRequest){
        return ResponseEntity.ok(authenticationService.signUpUser(signUpRequest));
    }
    @PostMapping("/signupadmin")
    public ResponseEntity<Usuario> signUpUAdmin(@RequestBody SignUpRequest signUpRequest){

        return ResponseEntity.ok(authenticationService.signUpAdmin(signUpRequest));
    }
    @GetMapping("/all")
    public ResponseEntity<List<Usuario>> getAll(){
        return ResponseEntity.ok(authenticationService.todos());
    }

    @GetMapping("/clave")
    public ResponseEntity<String> getClave(){
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String dato = Base64.getEncoder().encodeToString(key.getEncoded());
        return ResponseEntity.ok(dato);
    }

    @PostMapping("/login")
    public ResponseEntity<SignInResponse> login(@RequestBody SignInRequest signInRequest){
        return ResponseEntity.ok(authenticationService.signIn(signInRequest));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<SignInResponse> refreshtoken(@RequestParam String refreshToken) throws IllegalAccessException {
        return ResponseEntity.ok(authenticationService.getTokenByRefreshToken(refreshToken));
    }


    @GetMapping("/saludar")
    public  ResponseEntity<String> saludar () {
        return  ResponseEntity.ok("Hola este es un saludo");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse response = authenticationService.register(registerRequest);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/listar")
    public  ResponseEntity<List<Usuario>> listarUsuarios () {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return  ResponseEntity.ok(usuarios);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String tokenHeader = request.getHeader("Authorization");

            if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "message", "Token no proporcionado"));
            }

            String token = tokenHeader.substring(7);

            Map<String, Object> response = authenticationService.validateTokenUserInfo(token);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", e.getMessage()));

        } catch (Exception e) {
            log.error("Error en endpoint validate: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Error interno del servidor"));
        }
    }
}
