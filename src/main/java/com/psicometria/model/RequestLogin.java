package com.psicometria.model;

public class RequestLogin {

    private String correo;
    private String password;

    // Constructor
    public RequestLogin() {}

    // Constructor con los campos necesarios para iniciar sesión
    public RequestLogin(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }

    // Getters y Setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}