package com.vertice.verticecontrol.controller;

import com.vertice.verticecontrol.model.Cliente;
import com.vertice.verticecontrol.repository.ClienteRepository;
import com.vertice.verticecontrol.repository.VentaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;

    public ClienteController(ClienteRepository clienteRepository, VentaRepository ventaRepository) {
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
    }

    @GetMapping
    public String listarClientes(Model model,
                                 @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("error", error);
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@Valid @ModelAttribute("cliente") Cliente cliente,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "clientes/formulario";
        }
        clienteRepository.save(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de cliente inválido: " + id));
        model.addAttribute("cliente", cliente);
        return "clientes/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de cliente inválido: " + id));

        if (ventaRepository.existsByCliente(cliente)) {
            return "redirect:/clientes?error=No se puede eliminar el cliente porque tiene ventas asociadas.";
        }

        clienteRepository.delete(cliente);
        return "redirect:/clientes";
    }
}