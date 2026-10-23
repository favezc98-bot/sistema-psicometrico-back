package com.psicometria.model;

public class ResponseConsultarUsuario {

        private Integer idUsuario;
        private Integer idRolFk;
        private String correo;
        private String password;
        private Integer activo;

        // Constructor vacío
        public ResponseConsultarUsuario() {}

        // Constructor con todos los campos
        public ResponseConsultarUsuario(Integer idUsuario, Integer idRolFk, String correo, String password, Integer activo) {
            this.idUsuario = idUsuario;
            this.idRolFk = idRolFk;
            this.correo = correo;
            this.password = password;
            this.activo = activo;
        }

        // Getters y Setters
        public Integer getIdUsuario() {
            return idUsuario;
        }

        public void setIdUsuario(Integer idUsuario) {
            this.idUsuario = idUsuario;
        }

        public Integer getIdRolFk() {
            return idRolFk;
        }

        public void setIdRolFk(Integer idRolFk) {
            this.idRolFk = idRolFk;
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

        public Integer getActivo() {
            return activo;
        }

        public void setActivo(Integer activo) {
            this.activo = activo;
        }
}
