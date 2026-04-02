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

import com.store.pdvapi.dto.mesa.AtualizarStatusMesaRequest;
import com.store.pdvapi.dto.mesa.CriarMesaRequest;
import com.store.pdvapi.dto.mesa.MesaResponse;
import com.store.pdvapi.dto.mesa.MesaTotalResponse;
import com.store.pdvapi.dto.pedido.PedidoResponse;
import com.store.pdvapi.service.MesaService;
import com.store.pdvapi.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @GetMapping("/{id}/total")
    public MesaTotalResponse total(
            @Parameter(description = "ID da mesa cujo total deve ser calculado", required = true)
            @PathVariable Long id) {
        return service.calcularTotal(id);
    }

    @Operation(summary = "Listar pedidos da mesa", description = "Retorna os pedidos associados à mesa solicitada.")
    @GetMapping("/{id}/pedidos")
    public List<PedidoResponse> listarPedidos(
            @Parameter(description = "ID da mesa cujos pedidos devem ser listados", required = true)
            @PathVariable Long id) {
        service.buscarPorId(id);
        return pedidoService.listarPorMesa(id);
    }

    @Operation(summary = "Buscar mesa", description = "Retorna uma mesa específica pelo identificador.")
    @GetMapping("/{id}")
    public MesaResponse buscarPorId(
            @Parameter(description = "ID da mesa", required = true)
            @PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Abrir mesa", description = "Marca a mesa como ocupada e opcionalmente registra uma observação.")
    @PatchMapping("/{id}/abrir")
    public MesaResponse abrir(
            @Parameter(description = "ID da mesa a ser aberta", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Observação opcional para abertura.")
            @RequestBody(required = false) AtualizarStatusMesaRequest request) {
        return service.abrir(id, request);
    }

    @Operation(summary = "Fechar mesa", description = "Registra o fechamento de uma mesa e adiciona uma observação opcional.")
    @PatchMapping("/{id}/fechar")
    public MesaResponse fechar(
            @Parameter(description = "ID da mesa a ser fechada", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Observação opcional para fechamento.")
            @RequestBody(required = false) AtualizarStatusMesaRequest request) {
        return service.fechar(id, request);
    }

    @Operation(summary = "Liberar mesa", description = "Volta a mesa para o status livre.")
    @PatchMapping("/{id}/liberar")
    public MesaResponse liberar(
            @Parameter(description = "ID da mesa a ser liberada", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Observação opcional para liberação.")
            @RequestBody(required = false) AtualizarStatusMesaRequest request) {
        return service.liberar(id, request);
    }
}
