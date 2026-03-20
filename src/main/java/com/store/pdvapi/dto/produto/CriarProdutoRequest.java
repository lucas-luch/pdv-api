package com.store.pdvapi.dto.produto;

public class CriarProdutoRequest {

    private String nome;
    private double preco;
    private boolean ativo;
    

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public double getPreco() {
        return preco;
    }
    public void setPreco(double preco) {
        this.preco = preco;
    }
    public boolean isAtivo() {
        return ativo;
    }
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    
}
