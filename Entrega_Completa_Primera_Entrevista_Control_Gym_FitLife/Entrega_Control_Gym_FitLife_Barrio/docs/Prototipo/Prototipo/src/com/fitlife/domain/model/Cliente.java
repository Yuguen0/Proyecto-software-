package com.fitlife.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Cliente {
    private String cedula;
    private String nombre;
    private String celular;
    private String plan;
    private LocalDate fechaVencimiento;
    private String estado;

    public Cliente(String cedula, String nombre, String celular, String plan, LocalDate fechaVencimiento) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.celular = celular;
        this.plan = plan;
        this.fechaVencimiento = fechaVencimiento;
        actualizarEstado();
    }

    public void actualizarEstado() {
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
        if (diasRestantes < 0) {
            this.estado = "Vencido";
        } else if (diasRestantes <= 3) {
            this.estado = "Por vencer";
        } else {
            this.estado = "Vigente";
        }
    }

    // Getters y Setters
    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getCelular() { return celular; }
    public String getPlan() { return plan; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public String getEstado() { return estado; }

    public void setPlan(String plan) { this.plan = plan; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
        actualizarEstado();
    }
}