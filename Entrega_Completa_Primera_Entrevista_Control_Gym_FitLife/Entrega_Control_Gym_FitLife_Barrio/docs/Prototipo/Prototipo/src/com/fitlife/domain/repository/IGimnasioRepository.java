package com.fitlife.domain.repository;

import com.fitlife.domain.model.Cliente;
import com.fitlife.domain.model.MovimientoCaja;
import java.util.List;

// Abstracción estricta del repositorio (Clean Architecture)
public interface IGimnasioRepository {
    List<Cliente> obtenerClientes();
    void guardarCliente(Cliente cliente);
    Cliente buscarPorCedula(String cedula);
    
    List<MovimientoCaja> obtenerMovimientos();
    void guardarMovimiento(MovimientoCaja movimiento);
}