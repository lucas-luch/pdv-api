package com.store.pdvapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.store.pdvapi.dto.produto.CriarProdutoRequest;
import com.store.pdvapi.exception.ProdutoNaoEncontradoException;
import com.store.pdvapi.exception.ProdutoStatusInvalidoException;
import com.store.pdvapi.mapper.ProdutoMapper;
import com.store.pdvapi.model.Produto;
import com.store.pdvapi.repository.ProdutoRepository;

class ProdutoServiceTest {

    private ProdutoService service;
    private RecordingProdutoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RecordingProdutoRepository();
        service = new ProdutoService(repository, new ProdutoMapper());
    }

    @Test
    void criar_deveConverterRequisicaoESalvarOProduto() {
        CriarProdutoRequest request = new CriarProdutoRequest();
        request.setNome("Café Especial");
        request.setPreco(13.5);
        request.setAtivo(true);

        var resposta = service.criar(request);

        assertEquals(1L, resposta.getId());
        assertEquals("Café Especial", resposta.getNome());
        assertEquals(13.5, resposta.getPreco(), 0.0001);
        assertTrue(resposta.isAtivo());

        assertEquals("Café Especial", repository.getLastSaved().getNome());
        assertEquals(13.5, repository.getLastSaved().getPreco(), 0.0001);
        assertTrue(repository.getLastSaved().isAtivo());
    }

    @Test
    void buscarPorId_quandoNaoExistir_lancaProdutoNaoEncontrado() {
        assertThrows(ProdutoNaoEncontradoException.class, () -> service.buscarPorId(5L));
    }

    @Test
    void buscarPorId_quandoExistir_retornaRespostaCorreta() {
        Produto existente = new Produto(5L, "Água", 3.0, false);
        repository.seed(existente);

        var resposta = service.buscarPorId(5L);

        assertEquals(5L, resposta.getId());
        assertEquals("Água", resposta.getNome());
        assertEquals(3.0, resposta.getPreco(), 0.0001);
        assertFalse(resposta.isAtivo());
    }

    @Test
    void ativar_quandoProdutoJaEstiverAtivo_lancaProdutoStatusInvalido() {
        Produto produto = new Produto(2L, "Suco", 7.5, true);
        repository.seed(produto);

        assertThrows(ProdutoStatusInvalidoException.class, () -> service.ativar(2L));
    }

    @Test
    void inativar_quandoProdutoJaEstiverInativo_lancaProdutoStatusInvalido() {
        Produto produto = new Produto(3L, "Água", 3.0, false);
        repository.seed(produto);

        assertThrows(ProdutoStatusInvalidoException.class, () -> service.inativar(3L));
    }

    private static class RecordingProdutoRepository implements ProdutoRepository {

        private final Map<Long, Produto> store = new LinkedHashMap<>();
        private long nextId = 1;
        private Produto lastSaved;

        Produto getLastSaved() {
            return lastSaved;
        }

        void seed(Produto produto) {
            store.put(produto.getId(), produto);
        }

        @Override
        public void salvar(Produto produto) {
            produto.setId(nextId);
            store.put(produto.getId(), produto);
            lastSaved = produto;
            nextId++;
        }

        @Override
        public void atualizar(Produto produto) {
            store.put(produto.getId(), produto);
        }

        @Override
        public Produto buscarPorId(Long id) {
            return store.get(id);
        }

        @Override
        public List<Produto> listarTodos() {
            return new ArrayList<>(store.values());
        }
    }
}
