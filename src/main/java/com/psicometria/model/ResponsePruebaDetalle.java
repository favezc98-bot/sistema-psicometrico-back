package com.psicometria.model;

public class ResponsePruebaDetalle {

    private String tipo;

    private String nombre;

    private int puntuacionNatural;

    private int puntuacionEstandar;

    private String percentil;

    private double puntuacionT;

    // Constructores, Getters y Setters
    public ResponsePruebaDetalle() {

    }

    public ResponsePruebaDetalle(String tipo, String nombre, int puntuacionNatural, int puntuacionEstandar, String percentil, double puntuacionT) {
        this.tipo = tipo;
        this.nombre = nombre;
        this.puntuacionNatural = puntuacionNatural;
        this.puntuacionEstandar = puntuacionEstandar;
        this.percentil = percentil;
        this.puntuacionT = puntuacionT;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntuacionNatural() {
        return puntuacionNatural;
    }

    public void setPuntuacionNatural(int puntuacionNatural) {
        this.puntuacionNatural = puntuacionNatural;
    }

    public int getPuntuacionEstandar() {
        return puntuacionEstandar;
    }

    public void setPuntuacionEstandar(int puntuacionEstandar) {
        this.puntuacionEstandar = puntuacionEstandar;
    }

    public String getPercentil() {
        return percentil;
    }

    public void setPercentil(String percentil) {
        this.percentil = percentil;
    }

    public double getPuntuacionT() {
        return puntuacionT;
    }

    public void setPuntuacionT(double puntuacionT) {
        this.puntuacionT = puntuacionT;
    }
}
