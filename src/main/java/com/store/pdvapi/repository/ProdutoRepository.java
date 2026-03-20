package com.store.pdvapi.repository;
import com.store.pdvapi.model.Produto;
import java.util.List;


public interface ProdutoRepository {

    void salvar(Produto produto);
    void atualizar(Produto produto);
    Produto buscarPorId(Long id);

    List<Produto> listarTodos();
    
}
