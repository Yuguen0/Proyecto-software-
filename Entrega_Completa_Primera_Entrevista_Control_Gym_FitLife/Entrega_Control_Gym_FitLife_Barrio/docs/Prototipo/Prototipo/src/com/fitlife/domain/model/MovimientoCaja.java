package com.fitlife.domain.model;

public class MovimientoCaja {
    private String tipo; // "Ingreso" o "Gasto"
    private String descripcion;
    private double valor;

    public MovimientoCaja(String tipo, String descripcion, double valor) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.valor = valor;
    }

    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public double getValor() { return valor; }
}