package com.vertice.verticecontrol.controller;

import com.vertice.verticecontrol.model.CierreCaja;
import com.vertice.verticecontrol.repository.CierreCajaRepository;
import com.vertice.verticecontrol.repository.ClienteRepository;
import com.vertice.verticecontrol.repository.GastoRepository;
import com.vertice.verticecontrol.repository.ProductoRepository;
import com.vertice.verticecontrol.repository.VentaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final GastoRepository gastoRepository;
    private final CierreCajaRepository cierreCajaRepository;

    public HomeController(ProductoRepository productoRepository,
                          ClienteRepository clienteRepository,
                          VentaRepository ventaRepository,
                          GastoRepository gastoRepository,
                          CierreCajaRepository cierreCajaRepository) {
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
        this.gastoRepository = gastoRepository;
        this.cierreCajaRepository = cierreCajaRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        long totalProductos = productoRepository.count();
        long totalClientes = clienteRepository.count();

        double totalVendido = ventaRepository.findAll()
                .stream()
                .mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0.0)
                .sum();

        double totalInvertido = gastoRepository.findAll()
                .stream()
                .mapToDouble(g -> g.getMonto() != null ? g.getMonto() : 0.0)
                .sum();

        long ventasPendientes = ventaRepository.findAll()
                .stream()
                .filter(v -> "PENDIENTE".equalsIgnoreCase(v.getEstadoPago()))
                .count();

        CierreCaja cierreAbierto = cierreCajaRepository.findByEstado("ABIERTO").orElse(null);

        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalVendido", totalVendido);
        model.addAttribute("totalInvertido", totalInvertido);
        model.addAttribute("ventasPendientes", ventasPendientes);
        model.addAttribute("cierreAbierto", cierreAbierto);

        return "index";
    }
}