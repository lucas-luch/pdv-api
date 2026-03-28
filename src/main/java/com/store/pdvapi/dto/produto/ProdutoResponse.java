package com.store.pdvapi.dto.produto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProdutoResponse", description = "Representação dos dados de produto retornados pela API.")
public class ProdutoResponse {

    @Schema(description = "Identificador único do produto", example = "1")
    private Long id;

    @Schema(description = "Nome do produto", example = "Suco natural")
    private String nome;

    @Schema(description = "Preço atual do produto", example = "7.9")
    private double preco;

    @Schema(description = "Indica se o produto está ativo", example = "true")
    private boolean ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
