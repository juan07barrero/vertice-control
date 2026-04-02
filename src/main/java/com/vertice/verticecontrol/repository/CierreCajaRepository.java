package com.vertice.verticecontrol.repository;

import com.vertice.verticecontrol.model.CierreCaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CierreCajaRepository extends JpaRepository<CierreCaja, Long> {

    Optional<CierreCaja> findByEstado(String estado);
}