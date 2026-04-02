package com.vertice.verticecontrol.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "abonos")
public class Abono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El monto es obligatorio")
    @Min(value = 1, message = "El monto debe ser mayor que cero")
    private Double monto;

    private String metodoPago;

    @Column(length = 500)
    private String observacion;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Double getMonto() {
        return monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public String getObservacion() {
        return observacion;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }
}