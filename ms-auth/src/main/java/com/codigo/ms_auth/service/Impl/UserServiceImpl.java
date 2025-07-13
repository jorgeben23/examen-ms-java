package com.codigo.ms_auth.service.Impl;

import com.codigo.ms_auth.repository.UsuarioRepository;
import com.codigo.ms_auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

                return usuarioRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en la BD"));
            }
        };
    }
}
