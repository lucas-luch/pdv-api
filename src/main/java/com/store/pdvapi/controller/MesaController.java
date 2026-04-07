package com.store.pdvapi.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.pdvapi.dto.error.ErroResponse;
import com.store.pdvapi.dto.mesa.AtualizarStatusMesaRequest;
import com.store.pdvapi.dto.mesa.CriarMesaRequest;
import com.store.pdvapi.dto.mesa.MesaResponse;
import com.store.pdvapi.dto.mesa.MesaTotalResponse;
import com.store.pdvapi.dto.pedido.PedidoResponse;
import com.store.pdvapi.service.MesaService;
import com.store.pdvapi.service.PedidoService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/mesas")
@Tag(name = "Mesas", description = "Controles de abertura, fechamento e liberação de mesas")
public class MesaController {

    private final MesaService service;
    private final PedidoService pedidoService;

    public MesaController(MesaService service, PedidoService pedidoService) {
        this.service = service;
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Criar mesa", description = "Registra uma nova mesa no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da mesa inválidos ou número já cadastrado", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "MesaInvalida", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Numero da mesa ja cadastrado\",\"path\":\"/mesas\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao criar a mesa", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoMesa", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao criar a mesa\",\"path\":\"/mesas\"}"))) })
    @PostMapping
    public MesaResponse criar(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Número e observações da mesa.", required = true)
            @Valid @RequestBody CriarMesaRequest request) {
        return service.criar(request);
    }

    @Operation(summary = "Listar mesas", description = "Retorna todas as mesas registradas com seus status.")
    @GetMapping
    public List<MesaResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Calcular total da mesa", description = "Soma os subtotais dos itens de todos os pedidos da mesa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total da mesa calculado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "MesaNaoEncontradaTotal", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Mesa nao encontrada com id: 1\",\"path\":\"/mesas/1/total\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao calcular o total da mesa", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoTotalMesa", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao calcular o total da mesa\",\"path\":\"/mesas/1/total\"}"))) })
    @GetMapping("/{id}/total")
    public MesaTotalResponse total(
            @Parameter(description = "ID da mesa cujo total deve ser calculado", required = true)
            @PathVariable Long id) {
        return service.calcularTotal(id);
    }

    @Operation(summary = "Listar pedidos da mesa", description = "Retorna os pedidos associados à mesa solicitada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos da mesa listados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "MesaNaoEncontradaPedidos", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Mesa nao encontrada com id: 1\",\"path\":\"/mesas/1/pedidos\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao listar os pedidos da mesa", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoPedidosMesa", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao listar os pedidos da mesa\",\"path\":\"/mesas/1/pedidos\"}"))) })
    @GetMapping("/{id}/pedidos")
    public List<PedidoResponse> listarPedidos(
            @Parameter(description = "ID da mesa cujos pedidos devem ser listados", required = true)
            @PathVariable Long id) {
        service.buscarPorId(id);
        return pedidoService.listarPorMesa(id);
    }

    @Operation(summary = "Buscar mesa", description = "Retorna uma mesa específica pelo identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesa encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "MesaNaoEncontradaBusca", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Mesa nao encontrada com id: 1\",\"path\":\"/mesas/1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar a mesa", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoBuscaMesa", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao buscar a mesa\",\"path\":\"/mesas/1\"}"))) })
    @GetMapping("/{id}")
    public MesaResponse buscarPorId(
            @Parameter(description = "ID da mesa", required = true)
            @PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Abrir mesa", description = "Marca a mesa como ocupada e opcionalmente registra uma observação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesa aberta com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status da mesa inválido para abertura", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "StatusMesaInvalidoAbertura", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Status da mesa invalido para abertura\",\"path\":\"/mesas/1/abrir\"}"))),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "MesaNaoEncontradaAbertura", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Mesa nao encontrada com id: 1\",\"path\":\"/mesas/1/abrir\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao abrir a mesa", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoAberturaMesa", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao abrir a mesa\",\"path\":\"/mesas/1/abrir\"}"))) })
    @PatchMapping("/{id}/abrir")
    public MesaResponse abrir(
            @Parameter(description = "ID da mesa a ser aberta", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Observação opcional para abertura.")
            @RequestBody(required = false) AtualizarStatusMesaRequest request) {
        return service.abrir(id, request);
    }

    @Operation(summary = "Fechar mesa", description = "Registra o fechamento de uma mesa e adiciona uma observação opcional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesa fechada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status da mesa inválido para fechamento", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "StatusMesaInvalidoFechamento", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Status da mesa invalido para fechamento\",\"path\":\"/mesas/1/fechar\"}"))),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "MesaNaoEncontradaFechamento", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Mesa nao encontrada com id: 1\",\"path\":\"/mesas/1/fechar\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao fechar a mesa", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoFechamentoMesa", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao fechar a mesa\",\"path\":\"/mesas/1/fechar\"}"))) })
    @PatchMapping("/{id}/fechar")
    public MesaResponse fechar(
            @Parameter(description = "ID da mesa a ser fechada", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Observação opcional para fechamento.")
            @RequestBody(required = false) AtualizarStatusMesaRequest request) {
        return service.fechar(id, request);
    }

    @Operation(summary = "Liberar mesa", description = "Volta a mesa para o status livre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesa liberada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status da mesa inválido para liberação", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "StatusMesaInvalidoLiberacao", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Status da mesa invalido para liberacao\",\"path\":\"/mesas/1/liberar\"}"))),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "MesaNaoEncontradaLiberacao", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Mesa nao encontrada com id: 1\",\"path\":\"/mesas/1/liberar\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao liberar a mesa", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoLiberacaoMesa", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao liberar a mesa\",\"path\":\"/mesas/1/liberar\"}"))) })
    @PatchMapping("/{id}/liberar")
    public MesaResponse liberar(
            @Parameter(description = "ID da mesa a ser liberada", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Observação opcional para liberação.")
            @RequestBody(required = false) AtualizarStatusMesaRequest request) {
        return service.liberar(id, request);
    }
}
