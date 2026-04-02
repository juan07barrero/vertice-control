package com.vertice.verticecontrol.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor que cero")
    private Integer cantidad;

    private Double precioUnitario;

    private Double total;

    @NotNull(message = "El estado de pago es obligatorio")
    private String estadoPago;

    @Column(length = 500)
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "cierre_caja_id")
    private CierreCaja cierreCaja;

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Producto getProducto() {
        return producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public Double getTotal() {
        return total;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public String getObservaciones() {
        return observaciones;
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

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setCierreCaja(CierreCaja cierreCaja) {
        this.cierreCaja = cierreCaja;
    }
}