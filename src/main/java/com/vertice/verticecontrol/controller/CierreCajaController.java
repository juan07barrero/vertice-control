package com.vertice.verticecontrol.controller;

import com.vertice.verticecontrol.model.CierreCaja;
import com.vertice.verticecontrol.model.Venta;
import com.vertice.verticecontrol.repository.CierreCajaRepository;
import com.vertice.verticecontrol.repository.GastoRepository;
import com.vertice.verticecontrol.repository.VentaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/cierres")
public class CierreCajaController {

    private final CierreCajaRepository cierreCajaRepository;
    private final GastoRepository gastoRepository;
    private final VentaRepository ventaRepository;

    public CierreCajaController(CierreCajaRepository cierreCajaRepository,
                                GastoRepository gastoRepository,
                                VentaRepository ventaRepository) {
        this.cierreCajaRepository = cierreCajaRepository;
        this.gastoRepository = gastoRepository;
        this.ventaRepository = ventaRepository;
    }

    @GetMapping
    public String listarCierres(Model model) {
        model.addAttribute("cierres", cierreCajaRepository.findAll());
        return "cierres/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        CierreCaja cierre = new CierreCaja();
        cierre.setFechaInicio(LocalDate.now());
        cierre.setEstado("ABIERTO");
        model.addAttribute("cierre", cierre);
        return "cierres/formulario";
    }

    @PostMapping("/guardar")
    public String guardarCierre(@Valid @ModelAttribute("cierre") CierreCaja cierre,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "cierres/formulario";
        }

        if ("ABIERTO".equalsIgnoreCase(cierre.getEstado())) {
            cierreCajaRepository.findByEstado("ABIERTO").ifPresent(c -> {
                throw new IllegalStateException("Ya existe un cierre abierto.");
            });
        }

        cierreCajaRepository.save(cierre);
        return "redirect:/cierres";
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        CierreCaja cierre = cierreCajaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de cierre inválido: " + id));

        Double totalInvertido = gastoRepository.findByCierreCaja(cierre)
                .stream()
                .mapToDouble(g -> g.getMonto() != null ? g.getMonto() : 0.0)
                .sum();

        List<Venta> ventas = ventaRepository.findByCierreCaja(cierre);

        Double totalVendido = ventas.stream()
                .mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0.0)
                .sum();

        Double beneficio = totalVendido - totalInvertido;

        model.addAttribute("cierre", cierre);
        model.addAttribute("gastos", gastoRepository.findByCierreCaja(cierre));
        model.addAttribute("ventas", ventas);
        model.addAttribute("totalInvertido", totalInvertido);
        model.addAttribute("totalVendido", totalVendido);
        model.addAttribute("beneficio", beneficio);

        return "cierres/detalle";
    }

    @GetMapping("/cerrar/{id}")
    public String cerrarCierre(@PathVariable Long id) {
        CierreCaja cierre = cierreCajaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de cierre inválido: " + id));

        cierre.setEstado("CERRADO");
        cierre.setFechaFin(LocalDate.now());
        cierreCajaRepository.save(cierre);

        return "redirect:/cierres";
    }
}