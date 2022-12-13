package com.crizacio.controldeteccion;

public class Evento {
    private String titulo;
    private String descripcion;

    public Evento() {}

    public Evento(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public String gettitulo() {
        return titulo;
    }

    public String getdescripcion() {
        return descripcion;
    }
}
