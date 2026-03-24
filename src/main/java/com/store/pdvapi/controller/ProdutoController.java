package com.store.pdvapi.controller;

import java.util.List;

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

@RestController
@RequestMapping("/produtos")

public class ProdutoController {
    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @PostMapping
    public ProdutoResponse criar(@RequestBody CriarProdutoRequest request) {
        return service.criar(request);
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping
    public List<ProdutoResponse> listar() {
        return service.listar();
    }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(
            @PathVariable Long id,
            @RequestBody AtualizarProdutoRequest request) {
        return service.atualizar(id, request);
    }

    @PatchMapping("/{id}/ativar")
    public ProdutoResponse ativar(@PathVariable Long id) {
        return service.ativar(id);
    }

    @PatchMapping("/{id}/inativar")
    public ProdutoResponse inativar(@PathVariable Long id) {
        return service.inativar(id);
    }

}
