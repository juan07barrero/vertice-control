package com.vertice.verticecontrol.repository;

import com.vertice.verticecontrol.model.CierreCaja;
import com.vertice.verticecontrol.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByCierreCaja(CierreCaja cierreCaja);
}