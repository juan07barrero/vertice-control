package com.vertice.verticecontrol.repository;

import com.vertice.verticecontrol.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}