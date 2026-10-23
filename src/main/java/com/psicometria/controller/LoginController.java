package com.psicometria.controller;

import com.psicometria.model.RequestLogin;
import com.psicometria.model.ResponseConsultarUsuario;
import com.psicometria.service.UsuarioService;

public class LoginController {

    private UsuarioService usuarioService;

    public LoginController() {
        this.usuarioService = new UsuarioService();
    }

    public ResponseConsultarUsuario autenticar(RequestLogin requestLogin){

        if(requestLogin.getCorreo() == null || requestLogin.getPassword() == null){
            System.out.println("El correo y la contraseña son obligatorios.");
            return null;
        }

        // En lugar de devolver un boolean, le pedimos al service el usuario completo
        ResponseConsultarUsuario consultarUsuario = usuarioService.validarCredenciales(requestLogin);

        if (consultarUsuario != null) {
            System.out.println("Bienvenido al sistema! Rol: " + consultarUsuario.getIdRolFk());
            return consultarUsuario; // Retorna el objeto usuario con su id_usuario e id_rol_fk
        } else {
            System.out.println("Correo o contraseña incorrectos.");
            return null;
        }
    }
}