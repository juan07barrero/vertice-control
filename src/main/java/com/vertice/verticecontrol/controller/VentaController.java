package com.vertice.verticecontrol.controller;

import com.vertice.verticecontrol.model.Abono;
import com.vertice.verticecontrol.model.CierreCaja;
import com.vertice.verticecontrol.model.Cliente;
import com.vertice.verticecontrol.model.Producto;
import com.vertice.verticecontrol.model.Venta;
import com.vertice.verticecontrol.repository.AbonoRepository;
import com.vertice.verticecontrol.repository.CierreCajaRepository;
import com.vertice.verticecontrol.repository.ClienteRepository;
import com.vertice.verticecontrol.repository.ProductoRepository;
import com.vertice.verticecontrol.repository.VentaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final CierreCajaRepository cierreCajaRepository;
    private final AbonoRepository abonoRepository;

    public VentaController(VentaRepository ventaRepository,
                           ClienteRepository clienteRepository,
                           ProductoRepository productoRepository,
                           CierreCajaRepository cierreCajaRepository,
                           AbonoRepository abonoRepository) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.cierreCajaRepository = cierreCajaRepository;
        this.abonoRepository = abonoRepository;
    }

    @GetMapping
    public String listarVentas(Model model,
                               @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("ventas", ventaRepository.findAll());

        Double totalVendido = ventaRepository.findAll()
                .stream()
                .mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0.0)
                .sum();

        model.addAttribute("totalVendido", totalVendido);
        model.addAttribute("error", error);
        return "ventas/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setEstadoPago("PENDIENTE");

        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        return "ventas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarVenta(@Valid @ModelAttribute("venta") Venta venta,
                               BindingResult result,
                               @RequestParam(value = "clienteId", required = false) Long clienteId,
                               @RequestParam(value = "productoId", required = false) Long productoId,
                               Model model) {

        if (clienteId == null) {
            result.reject("clienteId", "Debes seleccionar un cliente.");
        }

        if (productoId == null) {
            result.reject("productoId", "Debes seleccionar un producto.");
        }

        Cliente cliente = clienteId != null ? clienteRepository.findById(clienteId).orElse(null) : null;
        Producto producto = productoId != null ? productoRepository.findById(productoId).orElse(null) : null;

        if (clienteId != null && cliente == null) {
            result.reject("clienteId", "Debes seleccionar un cliente válido.");
        }

        if (productoId != null && producto == null) {
            result.reject("productoId", "Debes seleccionar un producto válido.");
        }

        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            return "ventas/formulario";
        }

        CierreCaja cierreAbierto = cierreCajaRepository.findByEstado("ABIERTO")
                .orElseThrow(() -> new IllegalStateException("No hay un cierre de caja abierto para registrar esta venta."));

        if (producto.getStock() < venta.getCantidad()) {
            result.rejectValue("cantidad", "error.venta", "No hay suficiente stock disponible.");
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            return "ventas/formulario";
        }

        venta.setCliente(cliente);
        venta.setProducto(producto);
        venta.setPrecioUnitario(producto.getPrecio());
        venta.setTotal(venta.getCantidad() * venta.getPrecioUnitario());
        venta.setCierreCaja(cierreAbierto);

        producto.setStock(producto.getStock() - venta.getCantidad());
        productoRepository.save(producto);

        ventaRepository.save(venta);
        return "redirect:/ventas";
    }

    @GetMapping("/detalle/{id}")
    public String verDetalleVenta(@PathVariable Long id, Model model) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de venta inválido: " + id));

        List<Abono> abonos = abonoRepository.findByVenta(venta);

        double totalAbonado = abonos.stream()
                .mapToDouble(a -> a.getMonto() != null ? a.getMonto() : 0.0)
                .sum();

        double saldoPendiente = venta.getTotal() - totalAbonado;

        model.addAttribute("venta", venta);
        model.addAttribute("abonos", abonos);
        model.addAttribute("totalAbonado", totalAbonado);
        model.addAttribute("saldoPendiente", saldoPendiente);

        return "ventas/detalle";
    }

    @GetMapping("/marcar-pagado/{id}")
    public String marcarComoPagado(@PathVariable Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de venta inválido: " + id));

        venta.setEstadoPago("PAGADO");
        ventaRepository.save(venta);

        return "redirect:/ventas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVenta(@PathVariable Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de venta inválido: " + id));

        if (abonoRepository.existsByVenta(venta)) {
            return "redirect:/ventas?error=No se puede eliminar la venta porque tiene abonos asociados.";
        }

        Producto producto = venta.getProducto();
        producto.setStock(producto.getStock() + venta.getCantidad());
        productoRepository.save(producto);

        ventaRepository.delete(venta);
        return "redirect:/ventas";
    }
}