package com.tripndream.hopeapp;

public class Publicacion {

    private int id;
    private int idUsuario;
    private String nombreUsuario;
    private int idZona;
    private String nombreZona;
    private String nombreDesaparecido;
    private String descripcion;
    private String ultimoVistazo;
    private String fechaRegistro;
    private String foto;
    private int validada;
    private Boolean guardado;

    public Publicacion(int id, int idUsuario, String nombreUsuario, int idZona, String nombreZona, String nombreDesaparecido, String descripcion, String ultimoVistazo, String fechaRegistro, String foto, int validada, Boolean guardado) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idZona = idZona;
        this.nombreZona = nombreZona;
        this.nombreDesaparecido = nombreDesaparecido;
        this.descripcion = descripcion;
        this.ultimoVistazo = ultimoVistazo;
        this.fechaRegistro = fechaRegistro;
        this.foto = foto;
        this.validada = validada;
        this.guardado = guardado;
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

    public int getIdZona() {
        return idZona;
    }

    public void setIdZona(int idZona) {
        this.idZona = idZona;
    }

    public String getNombreZona() {
        return nombreZona;
    }

    public void setNombreZona(String nombreZona) {
        this.nombreZona = nombreZona;
    }

    public String getNombreDesaparecido() {
        return nombreDesaparecido;
    }

    public void setNombreDesaparecido(String nombreDesaparecido) {
        this.nombreDesaparecido = nombreDesaparecido;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUltimoVistazo() {
        return ultimoVistazo;
    }

    public void setUltimoVistazo(String ultimoVistazo) {
        this.ultimoVistazo = ultimoVistazo;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getValidada() {
        return validada;
    }

    public void setValidada(int validada) {
        this.validada = validada;
    }

    public Boolean getGuardado() {
        return guardado;
    }

    public void setGuardado(Boolean guardado) {
        this.guardado = guardado;
    }
}
