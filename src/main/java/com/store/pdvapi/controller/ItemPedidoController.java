package com.store.pdvapi.controller;

import com.store.pdvapi.dto.itempedido.CriarItemPedidoRequest;
import com.store.pdvapi.dto.itempedido.ItemPedidoResponse;
import com.store.pdvapi.service.ItemPedidoService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item-pedidos")
public class ItemPedidoController {

    private final ItemPedidoService service;

    public ItemPedidoController(ItemPedidoService service) {
        this.service = service;
    }

    @PostMapping
    public ItemPedidoResponse adicionar(@RequestBody CriarItemPedidoRequest request) {
        return service.adicionar(request);
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<ItemPedidoResponse> listarPorPedido(@PathVariable Long pedidoId) {
        return service.listarPorPedido(pedidoId);
    }
}
