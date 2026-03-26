package com.store.pdvapi.mapper;

import com.store.pdvapi.dto.pedido.CriarPedidoRequest;
import com.store.pdvapi.dto.pedido.PedidoResponse;
import com.store.pdvapi.model.Mesa;
import com.store.pdvapi.model.Pedido;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    public Pedido toEntity(CriarPedidoRequest request) {
        Pedido pedido = new Pedido();
        Mesa mesa = new Mesa();
        mesa.setId(request.getMesaId());
        pedido.setMesa(mesa);
        return pedido;
    }

    public PedidoResponse toResponse(Pedido pedido) {
        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setMesaId(pedido.getMesa().getId());
        response.setStatus(pedido.getStatus());
        response.setDataCriacao(pedido.getDataCriacao());
        response.setHoraCriacao(pedido.getHoraCriacao());
        return response;
    }
}
