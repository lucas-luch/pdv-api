package com.store.pdvapi.dto.produto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(name = "AtualizarProdutoRequest", description = "Dados que podem ser alterados em um produto já cadastrado.")
public class AtualizarProdutoRequest {

    @Schema(description = "Nome atualizado do produto", example = "Refrigerante sabor limão")
    @NotBlank(message = "Nome do produto é obrigatório")
    private String nome;

    @Schema(description = "Novo preço unitário do produto", example = "14.0")
    @Positive(message = "Preço do produto deve ser maior que zero")
    private double preco;

    @Schema(description = "Indica se o produto deve ficar ativo após a atualização", example = "false")
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
