package com.vertice.verticecontrol.repository;

import com.vertice.verticecontrol.model.CierreCaja;
import com.vertice.verticecontrol.model.Cliente;
import com.vertice.verticecontrol.model.Producto;
import com.vertice.verticecontrol.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByCierreCaja(CierreCaja cierreCaja);

    boolean existsByProducto(Producto producto);

    boolean existsByCliente(Cliente cliente);
}