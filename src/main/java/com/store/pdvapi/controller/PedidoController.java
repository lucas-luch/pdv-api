package com.store.pdvapi.controller;

import com.store.pdvapi.dto.itempedido.ItemPedidoResponse;
import com.store.pdvapi.dto.pedido.CriarPedidoRequest;
import com.store.pdvapi.dto.pedido.PedidoResponse;
import com.store.pdvapi.service.ItemPedidoService;
import com.store.pdvapi.service.PedidoService;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Fluxos de abertura, consulta e fechamento de pedidos")
public class PedidoController {

    private final PedidoService service;
    private final ItemPedidoService itemPedidoService;

    public PedidoController(PedidoService service, ItemPedidoService itemPedidoService) {
        this.service = service;
        this.itemPedidoService = itemPedidoService;
    }

    @Operation(summary = "Criar pedido", description = "Abre um pedido para a mesa informada, assumindo que esteja ocupada.")
    @PostMapping
    public PedidoResponse criar(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "ID da mesa que receberá o pedido.", required = true)
            @Valid @RequestBody CriarPedidoRequest request) {
        return service.criar(request);
    }

    @Operation(summary = "Buscar pedido", description = "Retorna os dados do pedido identificado.")
    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(
            @Parameter(description = "ID do pedido", required = true)
            @PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Listar pedidos por mesa", description = "Lista os pedidos vinculados à mesa informada.")
    @GetMapping("/mesa/{mesaId}")
    public List<PedidoResponse> listarPorMesa(
            @Parameter(description = "ID da mesa cujos pedidos devem ser listados", required = true)
            @PathVariable Long mesaId) {
        return service.listarPorMesa(mesaId);
    }

    @Operation(summary = "Fechar pedido", description = "Finaliza o pedido e altera o status para fechado.")
    @PatchMapping("/{id}/fechar")
    public PedidoResponse fechar(
            @Parameter(description = "ID do pedido a ser fechado", required = true)
            @PathVariable Long id) {
        return service.fechar(id);
    }

    @Operation(summary = "Listar itens do pedido", description = "Retorna os itens lançados no pedido informado.")
    @GetMapping("/{id}/itens")
    public List<ItemPedidoResponse> listarItens(
            @Parameter(description = "ID do pedido cujos itens devem ser listados", required = true)
            @PathVariable Long id) {
        service.buscarPorId(id);
        return itemPedidoService.listarPorPedido(id);
    }
}
