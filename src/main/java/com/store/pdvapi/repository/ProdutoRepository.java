package com.store.pdvapi.repository;

import java.util.List;

import com.store.pdvapi.model.Produto;

public interface ProdutoRepository {

    void salvar(Produto produto);
    void atualizar(Produto produto);
    Produto buscarPorId(Long id);

    List<Produto> listarTodos();
    
}
