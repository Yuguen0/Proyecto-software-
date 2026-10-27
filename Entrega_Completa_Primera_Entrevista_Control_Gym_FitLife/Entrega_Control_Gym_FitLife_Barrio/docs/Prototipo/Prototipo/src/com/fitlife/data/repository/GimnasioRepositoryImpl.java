package com.fitlife.data.repository;

import com.fitlife.domain.model.Cliente;
import com.fitlife.domain.model.MovimientoCaja;
import com.fitlife.domain.repository.IGimnasioRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Implementación concreta del repositorio en memoria para el prototipo
public class GimnasioRepositoryImpl implements IGimnasioRepository {
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<MovimientoCaja> movimientos = new ArrayList<>();

    public GimnasioRepositoryImpl() {
        // Datos iniciales de prueba
        clientes.add(new Cliente("1001234567", "Carlos Pérez", "3101234567", "Mensualidad", LocalDate.now().plusDays(15)));
        clientes.add(new Cliente("1009876543", "Ana Gómez", "3209876543", "Quincena", LocalDate.now().plusDays(1)));
        clientes.add(new Cliente("1005554443", "Luis Torres", "3005554443", "Día", LocalDate.now().minusDays(2)));

        movimientos.add(new MovimientoCaja("Ingreso", "Membresía Mensual - Carlos Pérez", 60000.0));
        movimientos.add(new MovimientoCaja("Ingreso", "Membresía Quincena - Ana Gómez", 35000.0));
        movimientos.add(new MovimientoCaja("Gasto", "Compra de suministros de aseo", 15000.0));
    }

    @Override
    public List<Cliente> obtenerClientes() { return clientes; }

    @Override
    public void guardarCliente(Cliente cliente) { clientes.add(cliente); }

    @Override
    public Cliente buscarPorCedula(String cedula) {
        return clientes.stream().filter(c -> c.getCedula().equals(cedula)).findFirst().orElse(null);
    }

    @Override
    public List<MovimientoCaja> obtenerMovimientos() { return movimientos; }

    @Override
    public void guardarMovimiento(MovimientoCaja movimiento) { movimientos.add(movimiento); }
}