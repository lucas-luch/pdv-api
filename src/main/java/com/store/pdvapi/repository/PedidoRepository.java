package com.store.pdvapi.repository;

import com.store.pdvapi.model.Pedido;
import java.util.List;

public interface PedidoRepository {

    void salvar(Pedido pedido);

    void atualizar(Pedido pedido);

    Pedido buscarPorId(Long id);

    List<Pedido> listarPorMesa(Long mesaId);
}
