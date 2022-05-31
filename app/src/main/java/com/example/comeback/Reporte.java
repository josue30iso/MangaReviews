package com.example.comeback;

import java.io.Serializable;

public class Reporte implements Serializable {
    private int id, spRaza, idColonia, estatus;
    private double recompensa;
    private String usuario, imagen, nombre, raza, colonia, descripcion, fecha, celular;

    public Reporte(int id, double recompensa, String usuario, int estatus, String imagen, String nombre, int spRaza, String raza, int idColonia, String colonia, String descripcion, String ultimaVista, String celular) {
        this.id = id;
        this.recompensa = recompensa;
        this.usuario = usuario;
        this.estatus = estatus;
        this.imagen = imagen;
        this.nombre = nombre;
        this.spRaza = spRaza;
        this.raza = raza;
        this.idColonia = idColonia;
        this.colonia = colonia;
        this.descripcion = descripcion;
        this.fecha = ultimaVista;
        this.celular = celular;
    }

    public Reporte() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public int getSpRaza() {
        return spRaza;
    }

    public void setSpRaza(int spRaza) {
        this.spRaza = spRaza;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getIdColonia() {
        return idColonia;
    }

    public void setIdColonia(int idColonia) {
        this.idColonia = idColonia;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public double getRecompensa() {
        return recompensa;
    }

    public void setRecompensa(double recompensa) {
        this.recompensa = recompensa;
    }
}
