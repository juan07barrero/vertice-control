package com.vertice.verticecontrol.controller;

import com.vertice.verticecontrol.model.CierreCaja;
import com.vertice.verticecontrol.model.Gasto;
import com.vertice.verticecontrol.repository.CierreCajaRepository;
import com.vertice.verticecontrol.repository.GastoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/gastos")
public class GastoController {

    private final GastoRepository gastoRepository;
    private final CierreCajaRepository cierreCajaRepository;

    public GastoController(GastoRepository gastoRepository, CierreCajaRepository cierreCajaRepository) {
        this.gastoRepository = gastoRepository;
        this.cierreCajaRepository = cierreCajaRepository;
    }

    @GetMapping
    public String listarGastos(Model model) {
        model.addAttribute("gastos", gastoRepository.findAll());

        Double totalInvertido = gastoRepository.findAll()
                .stream()
                .mapToDouble(Gasto::getMonto)
                .sum();

        model.addAttribute("totalInvertido", totalInvertido);
        return "gastos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Gasto gasto = new Gasto();
        gasto.setFecha(LocalDate.now());
        model.addAttribute("gasto", gasto);
        return "gastos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarGasto(@Valid @ModelAttribute("gasto") Gasto gasto,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "gastos/formulario";
        }

        CierreCaja cierreAbierto = cierreCajaRepository.findByEstado("ABIERTO")
                .orElseThrow(() -> new IllegalStateException("No hay un cierre de caja abierto para asignar este gasto."));

        gasto.setCierreCaja(cierreAbierto);
        gastoRepository.save(gasto);
        return "redirect:/gastos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de gasto inválido: " + id));
        model.addAttribute("gasto", gasto);
        return "gastos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarGasto(@PathVariable Long id) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de gasto inválido: " + id));
        gastoRepository.delete(gasto);
        return "redirect:/gastos";
    }
}