package com.store.pdvapi.dto.itempedido;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CriarItemPedidoRequest", description = "Corpo enviado ao adicionar um produto a um pedido aberto.")
public class CriarItemPedidoRequest {

    @Schema(description = "ID do pedido que receberá o novo item", example = "1")
    private Long pedidoId;

    @Schema(description = "ID do produto que será adicionado", example = "5")
    private Long produtoId;

    @Schema(description = "Quantidade desejada deste item", example = "2")
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
