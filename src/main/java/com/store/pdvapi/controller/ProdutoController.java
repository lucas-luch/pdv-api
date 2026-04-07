package com.store.pdvapi.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.pdvapi.dto.error.ErroResponse;
import com.store.pdvapi.dto.produto.AtualizarProdutoRequest;
import com.store.pdvapi.dto.produto.CriarProdutoRequest;
import com.store.pdvapi.dto.produto.ProdutoResponse;
import com.store.pdvapi.service.ProdutoService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/produtos")
@Tag(name = "Produtos", description = "Operações de cadastro, consulta e status de produtos")
public class ProdutoController {
    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar produto", description = "Cria um novo produto e retorna sua representação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados do produto inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ProdutoInvalido", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Nome do produto e obrigatorio\",\"path\":\"/produtos\",\"details\":[\"nome: nao deve estar em branco\"]}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar o cadastro do produto", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoProduto", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro ao cadastrar produto\",\"path\":\"/produtos\"}"))) })
    @PostMapping
    public ProdutoResponse criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload com nome, preço e flag de ativo.", required = true)
            @Valid @RequestBody CriarProdutoRequest request) {
        return service.criar(request);
    }

    @Operation(summary = "Buscar produto", description = "Retorna os dados completos de um produto pelo identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ProdutoNaoEncontrado", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Produto nao encontrado com id: 1\",\"path\":\"/produtos/1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar o produto", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoBuscaProduto", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao buscar o produto\",\"path\":\"/produtos/1\"}"))) })
    @GetMapping("/{id}")
    public ProdutoResponse buscarPorId(
            @Parameter(description = "ID do produto", required = true)
            @PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Listar produtos", description = "Retorna todos os produtos cadastrados, inclusive os inativos.")
    @GetMapping
    public List<ProdutoResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Atualizar produto", description = "Atualiza nome, preço e sinaliza se o produto continua ativo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados do produto inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "AtualizacaoProdutoInvalida", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Preco do produto deve ser maior que zero\",\"path\":\"/produtos/1\",\"details\":[\"preco: deve ser maior que zero\"]}"))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ProdutoNaoEncontradoAtualizacao", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Produto nao encontrado com id: 1\",\"path\":\"/produtos/1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao atualizar o produto", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoAtualizacaoProduto", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao atualizar o produto\",\"path\":\"/produtos/1\"}"))) })
    @PutMapping("/{id}")
    public ProdutoResponse atualizar(
            @Parameter(description = "ID do produto a ser atualizado", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados atualizados do produto.")
            @Valid @RequestBody AtualizarProdutoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Ativar produto", description = "Marca o produto como ativo sem alterar outros dados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto ativado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status do produto inválido para ativação", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "StatusProdutoInvalidoAtivacao", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Produto ja esta ativo\",\"path\":\"/produtos/1/ativar\"}"))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ProdutoNaoEncontradoAtivacao", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Produto nao encontrado com id: 1\",\"path\":\"/produtos/1/ativar\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao ativar o produto", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoAtivacaoProduto", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao ativar o produto\",\"path\":\"/produtos/1/ativar\"}"))) })
    @PatchMapping("/{id}/ativar")
    public ProdutoResponse ativar(
            @Parameter(description = "ID do produto a ser ativado", required = true)
            @PathVariable Long id) {
        return service.ativar(id);
    }

    @Operation(summary = "Inativar produto", description = "Marca o produto como inativo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto inativado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status do produto inválido para inativação", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "StatusProdutoInvalidoInativacao", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Produto ja esta inativo\",\"path\":\"/produtos/1/inativar\"}"))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ProdutoNaoEncontradoInativacao", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Produto nao encontrado com id: 1\",\"path\":\"/produtos/1/inativar\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao inativar o produto", content = @Content(schema = @Schema(implementation = ErroResponse.class), examples = @ExampleObject(name = "ErroInternoInativacaoProduto", value = "{\"timestamp\":\"2026-04-01T20:12:00\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Erro interno ao inativar o produto\",\"path\":\"/produtos/1/inativar\"}"))) })
    @PatchMapping("/{id}/inativar")
    public ProdutoResponse inativar(
            @Parameter(description = "ID do produto a ser inativado", required = true)
            @PathVariable Long id) {
        return service.inativar(id);
    }

}
