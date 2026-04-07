package com.store.pdvapi.controller;

import com.store.pdvapi.dto.error.ErroResponse;
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

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou status da mesa incompatível para abertura do pedido", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "PedidoInvalido", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Status da mesa incompativel para abertura do pedido\",\"path\":\"/pedidos\"}"))),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "MesaNaoEncontradaPedido", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Mesa nao encontrada com id: 1\",\"path\":\"/pedidos\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao criar o pedido", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoCriacaoPedido", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao criar o pedido\",\"path\":\"/pedidos\"}"))) })
    @PostMapping
    public PedidoResponse criar(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "ID da mesa que receberá o pedido.", required = true)
            @Valid @RequestBody CriarPedidoRequest request) {
        return service.criar(request);
    }

    @Operation(summary = "Buscar pedido", description = "Retorna os dados do pedido identificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "PedidoNaoEncontrado", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Pedido nao encontrado com id: 1\",\"path\":\"/pedidos/1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar o pedido", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoBuscaPedido", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao buscar o pedido\",\"path\":\"/pedidos/1\"}"))) })
    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(
            @Parameter(description = "ID do pedido", required = true)
            @PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Listar pedidos por mesa", description = "Lista os pedidos vinculados à mesa informada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos da mesa listados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao listar os pedidos da mesa", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoListaPedidosMesa", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao listar os pedidos da mesa\",\"path\":\"/pedidos/mesa/1\"}"))) })
    @GetMapping("/mesa/{mesaId}")
    public List<PedidoResponse> listarPorMesa(
            @Parameter(description = "ID da mesa cujos pedidos devem ser listados", required = true)
            @PathVariable Long mesaId) {
        return service.listarPorMesa(mesaId);
    }

    @Operation(summary = "Fechar pedido", description = "Finaliza o pedido e altera o status para fechado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido fechado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status do pedido inválido para fechamento", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "StatusPedidoInvalidoFechamento", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Status do pedido invalido para fechamento\",\"path\":\"/pedidos/1/fechar\"}"))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "PedidoNaoEncontradoFechamento", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Pedido nao encontrado com id: 1\",\"path\":\"/pedidos/1/fechar\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao fechar o pedido", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoFechamentoPedido", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao fechar o pedido\",\"path\":\"/pedidos/1/fechar\"}"))) })
    @PatchMapping("/{id}/fechar")
    public PedidoResponse fechar(
            @Parameter(description = "ID do pedido a ser fechado", required = true)
            @PathVariable Long id) {
        return service.fechar(id);
    }

    @Operation(summary = "Listar itens do pedido", description = "Retorna os itens lançados no pedido informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itens do pedido listados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "PedidoNaoEncontradoItens", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Pedido nao encontrado com id: 1\",\"path\":\"/pedidos/1/itens\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao listar os itens do pedido", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoItensPedido", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao listar os itens do pedido\",\"path\":\"/pedidos/1/itens\"}"))) })
    @GetMapping("/{id}/itens")
    public List<ItemPedidoResponse> listarItens(
            @Parameter(description = "ID do pedido cujos itens devem ser listados", required = true)
            @PathVariable Long id) {
        service.buscarPorId(id);
        return itemPedidoService.listarPorPedido(id);
    }
}
