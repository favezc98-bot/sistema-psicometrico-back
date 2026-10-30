package com.psicometria.model;

public class RequestRegistrarCandidato {

    private int id_usuario_fk;

    private String nombre;

    private String apellido;

    private String correo;

    private String password;

    private String telefono;

    private String domicilio;

    private String fecha_nacimiento;

    public RequestRegistrarCandidato(int id_usuario_fk, String nombre,
                                     String apellido, String correo, String password,
                                     String telefono, String domicilio, String fecha_nacimiento) {
        this.id_usuario_fk = id_usuario_fk;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.password = password;
        this.telefono = telefono;
        this.domicilio = domicilio;
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public RequestRegistrarCandidato() {
    }

    public int getId_usuario_fk() {
        return id_usuario_fk;
    }

    public void setId_usuario_fk(int id_usuario_fk) {
        this.id_usuario_fk = id_usuario_fk;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

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


    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }
}
