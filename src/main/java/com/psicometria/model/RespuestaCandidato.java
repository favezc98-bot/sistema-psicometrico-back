package modelo;

public class RespuestaCandidato {
    private int idRespuestaCandidato;
    private int idEvaluacionFk;
    private int idPreguntaTest;
    private String subParte;
    private String respuestaSeleccionada;

    public RespuestaCandidato() {}

    public RespuestaCandidato(int idEvaluacionFk, int idPreguntaTest, String subParte, String respuestaSeleccionada) {
        this.idEvaluacionFk = idEvaluacionFk;
        this.idPreguntaTest = idPreguntaTest;
        this.subParte = subParte;
        this.respuestaSeleccionada = respuestaSeleccionada;
    }

    // Getters y Setters corregidos a int
    public int getIdRespuestaCandidato() { return idRespuestaCandidato; }
    public void setIdRespuestaCandidato(int idRespuestaCandidato) { this.idRespuestaCandidato = idRespuestaCandidato; }

    public int getIdEvaluacionFk() { return idEvaluacionFk; }
    public void setIdEvaluacionFk(int idEvaluacionFk) { this.idEvaluacionFk = idEvaluacionFk; }

    public int getIdPreguntaTest() { return idPreguntaTest; }
    public void setIdPreguntaTest(int idPreguntaTest) { this.idPreguntaTest = idPreguntaTest; }

    public String getSubParte() { return subParte; }
    public void setSubParte(String subParte) { this.subParte = subParte; }

    public String getRespuestaSeleccionada() { return respuestaSeleccionada; }
    public void setRespuestaSeleccionada(String respuestaSeleccionada) { this.respuestaSeleccionada = respuestaSeleccionada; }
}