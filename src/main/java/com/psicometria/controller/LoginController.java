package com.psicometria.controller;

import com.psicometria.model.RequestLogin;
import com.psicometria.service.UsuarioService;

public class LoginController {

    private UsuarioService usuarioService;

    public LoginController() {
        this.usuarioService = new UsuarioService();
    }

    public boolean autenticar(RequestLogin requestLogin){

        if(requestLogin.getCorreo() == null || requestLogin.getPassword() == null){
            System.out.println("El correo y la contraseña son obligatorios.");
            return false;
        }

        boolean acceso = usuarioService.validarCredenciales(requestLogin);

        if (acceso) {
            System.out.println("Bienvenido al sistema! ");
            return true;
        } else {
            System.out.println("Correo o contraseña incorrectos.");
            return false;
        }
    }
}