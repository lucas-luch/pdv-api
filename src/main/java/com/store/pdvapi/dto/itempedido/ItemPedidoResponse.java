package com.store.pdvapi.dto.itempedido;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ItemPedidoResponse", description = "Detalhes de um item lançado em um pedido.")
public class ItemPedidoResponse {

    @Schema(description = "Identificador único do item de pedido", example = "15")
    private Long id;

    @Schema(description = "Pedido ao qual o item pertence", example = "1")
    private Long pedidoId;

    @Schema(description = "Produto incluído no pedido", example = "5")
    private Long produtoId;

    @Schema(description = "Quantidade lançada do produto", example = "2")
    private int quantidade;

    @Schema(description = "Preço unitário aplicado no momento do lançamento", example = "10.5")
    private double precoUnitario;

    @Schema(description = "Subtotal deste item (quantidade x preço)", example = "21.0")
    private double subtotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
