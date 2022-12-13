package com.crizacio.controldeteccion;

public class Dispositivo {
    private String nombre;
    private String direccion;

    public Dispositivo() {}

    public Dispositivo(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }
}
