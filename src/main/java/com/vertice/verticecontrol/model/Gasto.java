package com.vertice.verticecontrol.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "gastos")
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(length = 300)
    private String descripcion;

    @NotNull(message = "El monto es obligatorio")
    @Min(value = 0, message = "El monto no puede ser negativo")
    private Double monto;

    private String proveedor;

    @ManyToOne
    @JoinColumn(name = "cierre_caja_id")
    private CierreCaja cierreCaja;

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getMonto() {
        return monto;
    }

    public String getProveedor() {
        return proveedor;
    }

    public CierreCaja getCierreCaja() {
        return cierreCaja;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public void setCierreCaja(CierreCaja cierreCaja) {
        this.cierreCaja = cierreCaja;
    }
}