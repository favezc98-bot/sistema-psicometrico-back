package com.psicometria.model;

import java.util.List;

public class ResponseResultadoEvaluacion {

    private int idEvaluacion;

    private int edadEvaluado;

    private String combinacion;

    private String nombre;

    private String apellido;

    private List<ResponsePruebaDetalle> pruebas;

    private ResponseResultadoCombinado resultadoCombinado;

    private String categoria;

    private int puntajeTotal;

    // Constructores, Getters y Setters
    public ResponseResultadoEvaluacion() {

    }

    public int getIdEvaluacion() {
        return idEvaluacion;
    }
    public void setIdEvaluacion(int idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public int getEdadEvaluado() {
        return edadEvaluado;
    }
    public void setEdadEvaluado(int edadEvaluado) {
        this.edadEvaluado = edadEvaluado;
    }

    public String getCombinacion() {
        return combinacion;
    }
    public void setCombinacion(String combinacion) {
        this.combinacion = combinacion;
    }

    public List<ResponsePruebaDetalle> getPruebas() {
        return pruebas;
    }
    public void setPruebas(List<ResponsePruebaDetalle> pruebas) {
        this.pruebas = pruebas;
    }

    public ResponseResultadoCombinado getResultadoCombinado() {
        return resultadoCombinado;
    }
    public void setResultadoCombinado(ResponseResultadoCombinado resultadoCombinado) {
        this.resultadoCombinado = resultadoCombinado;
    }

    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getPuntajeTotal() {
        return puntajeTotal;
    }
    public void setPuntajeTotal(int puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
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
}