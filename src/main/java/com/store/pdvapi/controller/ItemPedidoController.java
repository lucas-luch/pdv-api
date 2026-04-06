package com.store.pdvapi.controller;

import com.store.pdvapi.dto.error.ErroResponse;
import com.store.pdvapi.dto.itempedido.CriarItemPedidoRequest;
import com.store.pdvapi.dto.itempedido.ItemPedidoResponse;
import com.store.pdvapi.service.ItemPedidoService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item-pedidos")
@Tag(name = "ItemPedido", description = "Inclui itens em um pedido e lista os itens já lançados")
public class ItemPedidoController {

    private final ItemPedidoService service;

    public ItemPedidoController(ItemPedidoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Adicionar item ao pedido",
               description = "Adiciona um produto ao pedido aberto definindo quantidade, preço e subtotal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item adicionado ao pedido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, pedido fechado ou produto inativo", content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido ou produto não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao adicionar o item ao pedido", content = @Content(schema = @Schema(implementation = ErroResponse.class))) })
    public ItemPedidoResponse adicionar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Pedido, produto e quantidade.")
            @Valid @RequestBody CriarItemPedidoRequest request) {
        return service.adicionar(request);
    }

    @Operation(summary = "Listar itens por pedido",
               description = "Retorna a lista de itens lançados em um pedido específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itens do pedido listados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao listar os itens do pedido", content = @Content(schema = @Schema(implementation = ErroResponse.class))) })
    @GetMapping("/pedido/{pedidoId}")
    public List<ItemPedidoResponse> listarPorPedido(
            @Parameter(description = "ID do pedido cujos itens serão listados", required = true)
            @PathVariable Long pedidoId) {
        return service.listarPorPedido(pedidoId);
    }
}
