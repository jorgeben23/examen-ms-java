package com.codigo.ms_auth.service.Impl;

import com.codigo.ms_auth.aggregates.constants.Constants;
import com.codigo.ms_auth.aggregates.request.RegisterRequest;
import com.codigo.ms_auth.aggregates.request.SignInRequest;
import com.codigo.ms_auth.aggregates.request.SignUpRequest;
import com.codigo.ms_auth.aggregates.response.RegisterResponse;
import com.codigo.ms_auth.aggregates.response.SignInResponse;
import com.codigo.ms_auth.entity.Rol;
import com.codigo.ms_auth.entity.Role;
import com.codigo.ms_auth.entity.Usuario;
import com.codigo.ms_auth.repository.RolRepository;
import com.codigo.ms_auth.repository.UsuarioRepository;
import com.codigo.ms_auth.service.AuthenticationService;
import com.codigo.ms_auth.service.JwtService;
import com.codigo.ms_auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;


    @Override
    public Usuario signUpUser(SignUpRequest signUpRequest) {

        Usuario usuario = getUsuarioEntity(signUpRequest);
        usuario.setRoles(Collections.singleton(getRoles(Role.USUARIO)));
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario signUpAdmin(SignUpRequest signUpRequest) {
        Usuario userAdmin = getUsuarioEntity(signUpRequest);
        Set<Rol> roles = new HashSet<>();
        roles.add(getRoles(Role.USUARIO));
        roles.add(getRoles(Role.ADMIN));
        userAdmin.setRoles(roles);
        return usuarioRepository.save(userAdmin);
    }

    @Override
    public List<Usuario> todos() {
        return usuarioRepository.findAll();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            String username = jwtService.extractUserName(token);
            UserDetails userDetails = userService.userDetailsService()
                    .loadUserByUsername(username);

            boolean result = jwtService.validateToken(token, userDetails)
                    && !jwtService.validateIsRefreshToken(token);

            log.info("Resultado de validación del token: " + result); // LOG PARA DEPURAR
            return result;
        } catch (Exception e) {
            log.info("Error al validar token: " + e.getMessage()); // LOG DE ERROR
            return false;
        }
    }

    @Override
    public SignInResponse signIn(SignInRequest signInRequest) {
        //Autenticación con las credenciales (eamil, password)
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail()
                , signInRequest.getPassword()));

        //Buscar al usuario en BD
        Usuario usuario = usuarioRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no econtrado en BD"));

        //Generamos los token
        String accessToken = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), usuario);

        //devuelvo la repsuesta
        return SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public SignInResponse getTokenByRefreshToken(String token) throws IllegalAccessException {
        //validar que el token es un refreshToken
        if (!jwtService.validateIsRefreshToken(token)) {
            throw new RuntimeException("Error: Token ingresado no es un RefreshToken");
        }
        //Extraer el username del token
        String userEmail = jwtService.extractUserName(token);

        //Buscar el usuario en la BD
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no econtrado en BD"));

        //Cargarmos los detalles del usuario
        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(usuario.getUsername());

        if (!jwtService.validateToken(token, userDetails)) {
            throw new IllegalAccessException("El refresh token no es valido o esta vencido");
        }

        //Generar el nuevo accesToken
        String newAccessToken = jwtService.generateToken(usuario);

        return SignInResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .build();

    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info("Si ingresa aqui al register");
            if (!Arrays.asList("SUPERADMIN", "ADMIN", "USUARIO").contains(registerRequest.getRol().toUpperCase())) {
                throw new IllegalArgumentException("Rol inválido. Use: SUPERADMIN, ADMIN, USUARIO");
            }
           Optional<Usuario> optUsuario = usuarioRepository.findByEmail(registerRequest.getEmail());
//            String usuarioJson = mapper.writeValueAsString(optUsuario.get());
//            log.info("usuario : {}", usuarioJson);
            if(optUsuario.isPresent()) {
                log.info("Si existe {}", optUsuario.get());
                throw new IllegalArgumentException("El email ya esta registrado");
            }
            Rol rol = rolRepository.findByNombreRol(registerRequest.getRol().toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + registerRequest.getRol()));

            // Crear usuario
            Usuario usuario = Usuario.builder()
                    .nombres(registerRequest.getNombre())
                    .email(registerRequest.getEmail())
                    .password(new BCryptPasswordEncoder().encode(registerRequest.getPassword()))
                    .numDoc(registerRequest.getNumDoc() != null ? registerRequest.getNumDoc() : "")
                    .roles(Collections.singleton(rol))
                    .isAccountNonExpired(Constants.STATUS_ACTIVE)
                    .isAccountNonLocked(Constants.STATUS_ACTIVE)
                    .isCredentialsNonExpired(Constants.STATUS_ACTIVE)
                    .isEnabled(Constants.STATUS_ACTIVE)
                    .build();

            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            return RegisterResponse.builder()
                    .message("Usuario registrado exitosamente")
                    .user(RegisterResponse.UserInfo.builder()
                            .id(usuarioGuardado.getId())
                            .nombre(usuarioGuardado.getNombres())
                            .email(usuarioGuardado.getEmail())
                            .rol(usuarioGuardado.getRoles().iterator().next().getNombreRol())
                            .build())
                    .build();

        } catch (Exception e) {
            log.error("Error registrando usuario: " + e.getMessage());
            throw new RuntimeException("Error registrando usuario: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> validateTokenUserInfo(String token) {
        log.info("inicio de token");
        try {
            boolean isValid = validateToken(token);
            if (!isValid) {
                throw new IllegalArgumentException("Token inválido o expirado");
            }
            log.info("Llega hasta aqui al menos");
            String username = jwtService.extractUserName(token);
            List<String> roles = jwtService.extractRoles(token);

            Usuario usuario = usuarioRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("user", Map.of(
                    "id", usuario.getId(),
                    "nombres", usuario.getNombres(),
                    "email", usuario.getEmail(),
                    "roles", roles
            ));

            return response;

        } catch (Exception e) {
            log.error("Error validando token: " + e.getMessage());
            throw new RuntimeException("Error validando token: " + e.getMessage());
        }
    }


    private Usuario getUsuarioEntity(SignUpRequest signUpRequest){
        return Usuario.builder()
                .nombres(signUpRequest.getNombres())
                .email(signUpRequest.getEmail())
                .password(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()))
                .numDoc(signUpRequest.getNumDoc())
                .isAccountNonExpired(Constants.STATUS_ACTIVE)
                .isAccountNonLocked(Constants.STATUS_ACTIVE)
                .isCredentialsNonExpired(Constants.STATUS_ACTIVE)
                .isEnabled(Constants.STATUS_ACTIVE)
                .build();
    }

    private Rol getRoles(Role rolBuscado){
        return rolRepository.findByNombreRol(rolBuscado.name())
                .orElseThrow(() -> new RuntimeException("Error el rol no exixte: " + rolBuscado.name()));
    }





}
