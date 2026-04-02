package com.vertice.verticecontrol.repository;

import com.vertice.verticecontrol.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}