package com.store.pdvapi.dto.produto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CriarProdutoRequest", description = "Payload necessário para cadastrar um novo produto.")
public class CriarProdutoRequest {

    @Schema(description = "Nome do produto", example = "Bebida gelada", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @Schema(description = "Preço unitário do produto", example = "12.5", requiredMode = Schema.RequiredMode.REQUIRED)
    private double preco;

    @Schema(description = "Flag que indica se o produto já entra ativo", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
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
