package com.store.pdvapi.dto.itempedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(name = "CriarItemPedidoRequest", description = "Corpo enviado ao adicionar um produto a um pedido aberto.")
public class CriarItemPedidoRequest {

    @Schema(description = "ID do pedido que receberá o novo item", example = "1")
    @NotNull(message = "Pedido é obrigatório")
    @Positive(message = "ID do pedido deve ser maior que zero")
    private Long pedidoId;

    @Schema(description = "ID do produto que será adicionado", example = "5")
    @NotNull(message = "Produto é obrigatório")
    @Positive(message = "ID do produto deve ser maior que zero")
    private Long produtoId;

    @Schema(description = "Quantidade desejada deste item", example = "2")
    @Positive(message = "Quantidade deve ser maior que zero")
    private int quantidade;

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
