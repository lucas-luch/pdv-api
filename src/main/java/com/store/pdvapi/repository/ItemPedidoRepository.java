package com.store.pdvapi.repository;

import com.store.pdvapi.model.ItemPedido;
import java.util.List;

public interface ItemPedidoRepository {

    void salvar(ItemPedido item);

    void atualizar(ItemPedido item);

    ItemPedido buscarPorId(Long id);

    List<ItemPedido> listarPorPedido(Long pedidoId);
}
