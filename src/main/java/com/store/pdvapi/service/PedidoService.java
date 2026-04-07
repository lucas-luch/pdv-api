package com.store.pdvapi.service;

import com.store.pdvapi.dto.pedido.CriarPedidoRequest;
import com.store.pdvapi.dto.pedido.PedidoResponse;
import com.store.pdvapi.enumtype.StatusMesa;
import com.store.pdvapi.enumtype.StatusPedido;
import com.store.pdvapi.exception.MesaNaoEncontradaException;
import com.store.pdvapi.exception.MesaStatusInvalidoException;
import com.store.pdvapi.exception.PedidoNaoEncontradoException;
import com.store.pdvapi.exception.PedidoStatusInvalidoException;
import com.store.pdvapi.mapper.PedidoMapper;
import com.store.pdvapi.model.Mesa;
import com.store.pdvapi.model.Pedido;
import com.store.pdvapi.repository.MesaRepository;
import com.store.pdvapi.repository.PedidoRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final PedidoMapper mapper;

    public PedidoService(PedidoRepository pedidoRepository, MesaRepository mesaRepository, PedidoMapper mapper) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.mapper = mapper;
    }

    public PedidoResponse criar(@Valid CriarPedidoRequest request) {
        Mesa mesa = buscarMesa(request.getMesaId());
        validarMesaParaAbrirPedido(mesa);

        Pedido pedido = mapper.toEntity(request);
        pedido.setMesa(mesa);
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setDataCriacao(LocalDate.now());
        pedido.setHoraCriacao(LocalTime.now());

        pedidoRepository.salvar(pedido);
        return mapper.toResponse(pedido);
    }

    public PedidoResponse buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.buscarPorId(id);
        if (pedido == null) {
            throw new PedidoNaoEncontradoException("Pedido não encontrado com id: " + id);
        }
        return mapper.toResponse(pedido);
    }

    public List<PedidoResponse> listarPorMesa(Long mesaId) {
        List<Pedido> pedidos = pedidoRepository.listarPorMesa(mesaId);
        List<PedidoResponse> responses = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            responses.add(mapper.toResponse(pedido));
        }
        return responses;
    }

    public PedidoResponse fechar(Long id) {
        Pedido pedido = pedidoRepository.buscarPorId(id);
        if (pedido == null) {
            throw new PedidoNaoEncontradoException("Pedido não encontrado com id: " + id);
        }
        validarPedidoAbertoParaFechar(pedido);

        pedido.setStatus(StatusPedido.FECHADO);
        pedidoRepository.atualizar(pedido);
        return mapper.toResponse(pedido);
    }

    private Mesa buscarMesa(Long mesaId) {
        Mesa mesa = mesaRepository.buscarPorId(mesaId);
        if (mesa == null) {
            throw new MesaNaoEncontradaException("Mesa não encontrada com id: " + mesaId);
        }
        return mesa;
    }

    private void validarMesaParaAbrirPedido(Mesa mesa) {
        if (mesa.getStatus() != StatusMesa.OCUPADA) {
            throw new MesaStatusInvalidoException("Só é possível abrir pedido em mesa ocupada");
        }
    }

    private void validarPedidoAbertoParaFechar(Pedido pedido) {
        if (pedido.getStatus() != StatusPedido.ABERTO) {
            throw new PedidoStatusInvalidoException("Somente pedidos abertos podem ser fechados");
        }
    }
}
