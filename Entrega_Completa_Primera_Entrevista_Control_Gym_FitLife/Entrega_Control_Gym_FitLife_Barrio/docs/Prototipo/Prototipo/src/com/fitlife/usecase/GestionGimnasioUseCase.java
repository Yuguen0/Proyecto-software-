package com.fitlife.usecase;

import com.fitlife.domain.model.Cliente;
import com.fitlife.domain.model.MovimientoCaja;
import com.fitlife.domain.repository.IGimnasioRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Orquestador de la lógica de negocio (Casos de Uso)
public class GestionGimnasioUseCase {
    private final IGimnasioRepository repository;

    public GestionGimnasioUseCase(IGimnasioRepository repository) {
        this.repository = repository;
    }

    public List<Cliente> obtenerTodosLosClientes() {
        return repository.obtenerClientes();
    }

    public List<Cliente> buscarClientes(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            return obtenerTodosLosClientes();
        }
        String lowerFiltro = filtro.toLowerCase();
        return repository.obtenerClientes().stream()
                .filter(c -> c.getCedula().toLowerCase().contains(lowerFiltro) ||
                             c.getNombre().toLowerCase().contains(lowerFiltro) ||
                             c.getCelular().toLowerCase().contains(lowerFiltro))
                .collect(Collectors.toList());
    }

    public void registrarClienteNuevo(String cedula, String nombre, String celular, String plan) {
        LocalDate vencimiento = LocalDate.now().plusDays(30);
        double valorPlan = 60000;

        if (plan.equals("Día")) {
            vencimiento = LocalDate.now();
            valorPlan = 8000;
        } else if (plan.equals("Quincena")) {
            vencimiento = LocalDate.now().plusDays(15);
            valorPlan = 35000;
        }

        Cliente nuevo = new Cliente(cedula, nombre, celular, plan, vencimiento);
        repository.guardarCliente(nuevo);

        // Registro económico automático en caja (RP-04)
        repository.guardarMovimiento(new MovimientoCaja("Ingreso", "Plan " + plan + " - " + nombre, valorPlan));
    }

    public void asignarPlan(String cedula, String nuevoPlan) {
        Cliente cliente = repository.buscarPorCedula(cedula);
        if (cliente != null) {
            cliente.setPlan(nuevoPlan);
            if (nuevoPlan.equals("Día")) cliente.setFechaVencimiento(LocalDate.now());
            else if (nuevoPlan.equals("Quincena")) cliente.setFechaVencimiento(LocalDate.now().plusDays(15));
            else cliente.setFechaVencimiento(LocalDate.now().plusDays(30));
        }
    }

    public List<MovimientoCaja> obtenerMovimientos() {
        return repository.obtenerMovimientos();
    }

    public void registrarMovimientoCaja(String tipo, String descripcion, double valor) {
        repository.guardarMovimiento(new MovimientoCaja(tipo, descripcion, valor));
    }

    public double calcularSaldoCaja() {
        double ingresos = repository.obtenerMovimientos().stream()
                .filter(m -> m.getTipo().equals("Ingreso"))
                .mapToDouble(MovimientoCaja::getValor).sum();
        double gastos = repository.obtenerMovimientos().stream()
                .filter(m -> m.getTipo().equals("Gasto"))
                .mapToDouble(MovimientoCaja::getValor).sum();
        return ingresos - gastos;
    }
}