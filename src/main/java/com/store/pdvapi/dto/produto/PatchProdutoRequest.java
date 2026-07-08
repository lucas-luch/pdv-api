package com.store.pdvapi.dto.produto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(name = "PatchProdutoRequest", description = "Campos opcionais para atualização parcial de produto. Apenas os campos presentes serão alterados.")
public class PatchProdutoRequest {

    @Schema(description = "Nome atualizado do produto", example = "Refrigerante sabor limão")
    @Size(min = 1, message = "Nome não pode estar em branco")
    private String nome;

    @Schema(description = "Novo preço unitário do produto", example = "14.0")
    @Positive(message = "Preço do produto deve ser maior que zero")
    private Double preco;

    @Schema(description = "Indica se o produto deve ficar ativo", example = "false")
    private Boolean ativo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
