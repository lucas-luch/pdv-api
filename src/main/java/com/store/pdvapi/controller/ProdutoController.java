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

import com.store.pdvapi.dto.produto.AtualizarProdutoRequest;
import com.store.pdvapi.dto.produto.CriarProdutoRequest;
import com.store.pdvapi.dto.produto.ProdutoResponse;
import com.store.pdvapi.service.ProdutoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @PostMapping
    public ProdutoResponse criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload com nome, preço e flag de ativo.", required = true)
            @Valid @RequestBody CriarProdutoRequest request) {
        return service.criar(request);
    }

    @Operation(summary = "Buscar produto", description = "Retorna os dados completos de um produto pelo identificador.")
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
    @PutMapping("/{id}")
    public ProdutoResponse atualizar(
            @Parameter(description = "ID do produto a ser atualizado", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados atualizados do produto.")
            @Valid @RequestBody AtualizarProdutoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Ativar produto", description = "Marca o produto como ativo sem alterar outros dados.")
    @PatchMapping("/{id}/ativar")
    public ProdutoResponse ativar(
            @Parameter(description = "ID do produto a ser ativado", required = true)
            @PathVariable Long id) {
        return service.ativar(id);
    }

    @Operation(summary = "Inativar produto", description = "Marca o produto como inativo.")
    @PatchMapping("/{id}/inativar")
    public ProdutoResponse inativar(
            @Parameter(description = "ID do produto a ser inativado", required = true)
            @PathVariable Long id) {
        return service.inativar(id);
    }

}
