package com.tripndream.hopeapp;

public class Comentario {

    private int id;
    private int idUsuario;
    private int idUsuarioPublicacion;
    private String nombreUsuario;
    private int idPublicacion;
    private String mensaje;
    private String fechaRegistro;

    public Comentario(int id, int idUsuario, int idUsuarioPublicacion, String nombreUsuario, int idPublicacion, String mensaje, String fechaRegistro) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idUsuarioPublicacion = idUsuarioPublicacion;
        this.nombreUsuario = nombreUsuario;
        this.idPublicacion = idPublicacion;
        this.mensaje = mensaje;
        this.fechaRegistro = fechaRegistro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(int idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdUsuarioPublicacion() {
        return idUsuarioPublicacion;
    }

    public void setIdUsuarioPublicacion(int idUsuarioPublicacion) {
        this.idUsuarioPublicacion = idUsuarioPublicacion;
    }
}
