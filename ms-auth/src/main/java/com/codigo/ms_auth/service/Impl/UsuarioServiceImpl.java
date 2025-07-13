package com.codigo.ms_auth.service.Impl;

import com.codigo.ms_auth.aggregates.request.UsuarioRequest;
import com.codigo.ms_auth.entity.Rol;
import com.codigo.ms_auth.entity.Usuario;
import com.codigo.ms_auth.repository.RolRepository;
import com.codigo.ms_auth.repository.UsuarioRepository;
import com.codigo.ms_auth.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;


    @Override
    public Usuario crearUsuario(UsuarioRequest usuario) {

        Set<Rol> roles = new HashSet<>();

        // Verificar que getRoles() no sea null
        if (usuario.getRoles() != null) {
            for (String nombreRol : usuario.getRoles()) {
                Rol rol = rolRepository.findByNombreRol(nombreRol)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol));
                roles.add(rol);
            }
        }

        // Cambiar usuario.builder() por Usuario.builder()
        Usuario newUsuario = Usuario.builder()
                .nombres(usuario.getNombres())
                .email(usuario.getEmail())
                .password(usuario.getPassword()) // (opcional: encriptar)
                .numDoc(usuario.getNumDoc())
                .roles(roles)
                .build();

        return usuarioRepository.save(newUsuario);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}
