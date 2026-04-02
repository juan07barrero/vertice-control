package com.vertice.verticecontrol.controller;

import com.vertice.verticecontrol.model.Producto;
import com.vertice.verticecontrol.repository.ProductoRepository;
import com.vertice.verticecontrol.repository.VentaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;

    public ProductoController(ProductoRepository productoRepository, VentaRepository ventaRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
    }

    @GetMapping
    public String listarProductos(Model model,
                                  @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("error", error);
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@Valid @ModelAttribute("producto") Producto producto,
                                  BindingResult result) {
        if (result.hasErrors()) {
            return "productos/formulario";
        }
        productoRepository.save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de producto inválido: " + id));
        model.addAttribute("producto", producto);
        return "productos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de producto inválido: " + id));

        if (ventaRepository.existsByProducto(producto)) {
            return "redirect:/productos?error=No se puede eliminar el producto porque tiene ventas asociadas.";
        }

        productoRepository.delete(producto);
        return "redirect:/productos";
    }
}