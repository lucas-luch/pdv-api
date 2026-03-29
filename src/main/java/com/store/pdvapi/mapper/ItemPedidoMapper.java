package com.store.pdvapi.mapper;

import com.store.pdvapi.dto.itempedido.CriarItemPedidoRequest;
import com.store.pdvapi.dto.itempedido.ItemPedidoResponse;
import com.store.pdvapi.model.ItemPedido;
import com.store.pdvapi.model.Pedido;
import com.store.pdvapi.model.Produto;
import org.springframework.stereotype.Component;

@Component
public class ItemPedidoMapper {

    public ItemPedido toEntity(CriarItemPedidoRequest request) {
        ItemPedido item = new ItemPedido();
        Pedido pedido = new Pedido();
        pedido.setId(request.getPedidoId());
        item.setPedido(pedido);
        Produto produto = new Produto();
        produto.setId(request.getProdutoId());
        item.setProduto(produto);
        item.setQuantidade(request.getQuantidade());
        return item;
    }

    public ItemPedidoResponse toResponse(ItemPedido item) {
        ItemPedidoResponse response = new ItemPedidoResponse();
        response.setId(item.getId());
        response.setPedidoId(item.getPedido().getId());
        response.setProdutoId(item.getProduto().getId());
        response.setQuantidade(item.getQuantidade());
        response.setPrecoUnitario(item.getPrecoUnitario());
        response.setSubtotal(item.getSubtotal());
        return response;
    }
}
