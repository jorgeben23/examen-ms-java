package com.codigo.ms_auth.service;

import com.codigo.ms_auth.aggregates.request.UsuarioRequest;
import com.codigo.ms_auth.entity.Usuario;

import java.util.List;

public interface UsuarioService {
    Usuario crearUsuario (UsuarioRequest usuario);
    List<Usuario> listarUsuarios ();
}
