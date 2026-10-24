package com.psicometria.model;

public class RequestRegistrarEvaluacion {

    private int id_candidato_fk;
    private String combinacion;
    private String nombre_evaluador;

    // Getters y Setters
    public int getId_candidato_fk() { return id_candidato_fk; }
    public void setId_candidato_fk(int id_candidato_fk) { this.id_candidato_fk = id_candidato_fk; }

    public String getCombinacion() { return combinacion; }
    public void setCombinacion(String combinacion) { this.combinacion = combinacion; }

    public String getNombre_evaluador() { return nombre_evaluador; }
    public void setNombre_evaluador(String nombre_evaluador) { this.nombre_evaluador = nombre_evaluador; }
}
