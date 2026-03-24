package com.store.pdvapi.mapper;

import org.springframework.stereotype.Component;

import com.store.pdvapi.dto.produto.CriarProdutoRequest;
import com.store.pdvapi.dto.produto.ProdutoResponse;
import com.store.pdvapi.model.Produto;

@Component
public class ProdutoMapper {

    public Produto toEntity(CriarProdutoRequest request){
        
        Produto produto = new Produto();

        produto.setNome(request.getNome());
        produto.setPreco(request.getPreco());
        produto.setAtivo(request.isAtivo());

        return produto;
    }

    public ProdutoResponse toResponse(Produto produto){

        ProdutoResponse response = new ProdutoResponse();

        response.setId(produto.getId());
        response.setNome(produto.getNome());
        response.setPreco(produto.getPreco());
        response.setAtivo(produto.isAtivo());

        return response;
    }


}
