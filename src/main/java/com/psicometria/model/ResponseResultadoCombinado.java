package com.psicometria.model;

public class ResponseResultadoCombinado {

    private int sumaPuntuacionesEstandar;

    private String puntuacionEstandar;

    private String percentil;

    private String categoria;

    public ResponseResultadoCombinado(int sumaPuntuacionesEstandar,
                                      String puntuacionEstandar,
                                      String percentil, String categoria) {

        this.sumaPuntuacionesEstandar = sumaPuntuacionesEstandar;
        this.puntuacionEstandar = puntuacionEstandar;
        this.percentil = percentil;
        this.categoria = categoria;
    }

    public ResponseResultadoCombinado() {

    }

    public int getSumaPuntuacionesEstandar() {
        return sumaPuntuacionesEstandar;
    }

    public void setSumaPuntuacionesEstandar(int sumaPuntuacionesEstandar) {
        this.sumaPuntuacionesEstandar = sumaPuntuacionesEstandar;
    }

    public String getPuntuacionEstandar() {
        return puntuacionEstandar;
    }

    public void setPuntuacionEstandar(String puntuacionEstandar) {
        this.puntuacionEstandar = puntuacionEstandar;
    }

    public String getPercentil() {
        return percentil;
    }

    public void setPercentil(String percentil) {
        this.percentil = percentil;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
