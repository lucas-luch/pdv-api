package com.store.pdvapi.controller;

import com.store.pdvapi.dto.pedido.CriarPedidoRequest;
import com.store.pdvapi.dto.pedido.PedidoResponse;
import com.store.pdvapi.service.PedidoService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public PedidoResponse criar(@RequestBody CriarPedidoRequest request) {
        return service.criar(request);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/mesa/{mesaId}")
    public List<PedidoResponse> listarPorMesa(@PathVariable Long mesaId) {
        return service.listarPorMesa(mesaId);
    }

    @PatchMapping("/{id}/fechar")
    public PedidoResponse fechar(@PathVariable Long id) {
        return service.fechar(id);
    }
}
