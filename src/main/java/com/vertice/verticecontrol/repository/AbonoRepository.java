package com.vertice.verticecontrol.repository;

import com.vertice.verticecontrol.model.Abono;
import com.vertice.verticecontrol.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbonoRepository extends JpaRepository<Abono, Long> {
    List<Abono> findByVenta(Venta venta);

    boolean existsByVenta(Venta venta);
}