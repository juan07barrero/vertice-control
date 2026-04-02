package com.vertice.verticecontrol.controller;

import com.vertice.verticecontrol.model.Abono;
import com.vertice.verticecontrol.model.Venta;
import com.vertice.verticecontrol.repository.AbonoRepository;
import com.vertice.verticecontrol.repository.VentaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/abonos")
public class AbonoController {

    private final AbonoRepository abonoRepository;
    private final VentaRepository ventaRepository;

    public AbonoController(AbonoRepository abonoRepository, VentaRepository ventaRepository) {
        this.abonoRepository = abonoRepository;
        this.ventaRepository = ventaRepository;
    }

    @GetMapping("/nuevo/{ventaId}")
    public String mostrarFormularioNuevo(@PathVariable Long ventaId, Model model) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Id de venta inválido: " + ventaId));

        Abono abono = new Abono();
        abono.setFecha(LocalDate.now());

        model.addAttribute("abono", abono);
        model.addAttribute("venta", venta);
        model.addAttribute("abonos", abonoRepository.findByVenta(venta));

        double totalAbonado = abonoRepository.findByVenta(venta)
                .stream()
                .mapToDouble(a -> a.getMonto() != null ? a.getMonto() : 0.0)
                .sum();

        double saldoPendiente = venta.getTotal() - totalAbonado;

        model.addAttribute("totalAbonado", totalAbonado);
        model.addAttribute("saldoPendiente", saldoPendiente);

        return "abonos/formulario";
    }

    @PostMapping("/guardar/{ventaId}")
    public String guardarAbono(@PathVariable Long ventaId,
                               @Valid @ModelAttribute("abono") Abono abono,
                               BindingResult result,
                               Model model) {

        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Id de venta inválido: " + ventaId));

        double totalAbonadoActual = abonoRepository.findByVenta(venta)
                .stream()
                .mapToDouble(a -> a.getMonto() != null ? a.getMonto() : 0.0)
                .sum();

        double saldoPendiente = venta.getTotal() - totalAbonadoActual;

        if (abono.getMonto() != null && abono.getMonto() > saldoPendiente) {
            result.rejectValue("monto", "error.abono", "El abono no puede superar el saldo pendiente.");
        }

        if (result.hasErrors()) {
            model.addAttribute("venta", venta);
            model.addAttribute("abonos", abonoRepository.findByVenta(venta));
            model.addAttribute("totalAbonado", totalAbonadoActual);
            model.addAttribute("saldoPendiente", saldoPendiente);
            return "abonos/formulario";
        }

        abono.setVenta(venta);
        abonoRepository.save(abono);

        double nuevoTotalAbonado = totalAbonadoActual + abono.getMonto();

        if (nuevoTotalAbonado <= 0) {
            venta.setEstadoPago("PENDIENTE");
        } else if (nuevoTotalAbonado < venta.getTotal()) {
            venta.setEstadoPago("ABONO");
        } else {
            venta.setEstadoPago("PAGADO");
        }

        ventaRepository.save(venta);

        return "redirect:/ventas/detalle/" + ventaId;
    }
}