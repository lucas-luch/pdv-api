package com.store.pdvapi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.store.pdvapi.dto.mesa.AtualizarStatusMesaRequest;
import com.store.pdvapi.dto.mesa.CriarMesaRequest;
import com.store.pdvapi.dto.mesa.MesaResponse;
import com.store.pdvapi.dto.mesa.MesaTotalResponse;
import com.store.pdvapi.enumtype.StatusMesa;
import com.store.pdvapi.exception.MesaNaoEncontradaException;
import com.store.pdvapi.exception.MesaNumeroDuplicadoException;
import com.store.pdvapi.exception.MesaNumeroObrigatorioException;
import com.store.pdvapi.exception.MesaStatusInvalidoException;
import com.store.pdvapi.mapper.MesaMapper;
import com.store.pdvapi.model.ItemPedido;
import com.store.pdvapi.model.Mesa;
import com.store.pdvapi.model.Pedido;
import com.store.pdvapi.repository.ItemPedidoRepository;
import com.store.pdvapi.repository.MesaRepository;
import com.store.pdvapi.repository.PedidoRepository;

@Service
public class MesaService {

    private final MesaRepository repository;
    private final MesaMapper mapper;
    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public MesaService(MesaRepository repository, MesaMapper mapper,
                       PedidoRepository pedidoRepository,
                       ItemPedidoRepository itemPedidoRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    public MesaResponse criar(CriarMesaRequest request) {
        String numero = validarNumero(request.getNumero());

        if (repository.buscarPorNumero(numero) != null) {
            throw new MesaNumeroDuplicadoException("Já existe uma mesa com número: " + numero);
        }

        Mesa mesa = mapper.toEntity(request);
        mesa.setNumero(numero);
        mesa.setStatus(StatusMesa.LIVRE);
        repository.salvar(mesa);

        return mapper.toResponse(mesa);
    }

    public MesaResponse buscarPorId(Long id) {
        Mesa mesa = buscarOuFalhar(id);
        return mapper.toResponse(mesa);
    }

    public List<MesaResponse> listar() {
        List<Mesa> mesas = repository.listarTodos();
        List<MesaResponse> responses = new ArrayList<>();

        for (Mesa mesa : mesas) {
            responses.add(mapper.toResponse(mesa));
        }

        return responses;
    }

    public MesaResponse abrir(Long id, AtualizarStatusMesaRequest request) {
        Mesa mesa = buscarOuFalhar(id);

        if (mesa.getStatus() != StatusMesa.LIVRE) {
            throw new MesaStatusInvalidoException("Mesa precisa estar livre para ser ocupada");
        }

        mesa.setStatus(StatusMesa.OCUPADA);
        atualizarObservacao(mesa, request);
        repository.atualizar(mesa);

        return mapper.toResponse(mesa);
    }

    public MesaResponse fechar(Long id, AtualizarStatusMesaRequest request) {
        Mesa mesa = buscarOuFalhar(id);

        if (mesa.getStatus() != StatusMesa.OCUPADA) {
            throw new MesaStatusInvalidoException("Somente mesas ocupadas podem ser fechadas");
        }

        mesa.setStatus(StatusMesa.FECHADA);
        atualizarObservacao(mesa, request);
        repository.atualizar(mesa);

        return mapper.toResponse(mesa);
    }

    public MesaResponse liberar(Long id, AtualizarStatusMesaRequest request) {
        Mesa mesa = buscarOuFalhar(id);

        if (mesa.getStatus() == StatusMesa.LIVRE) {
            throw new MesaStatusInvalidoException("Mesa já está livre");
        }

        mesa.setStatus(StatusMesa.LIVRE);
        atualizarObservacao(mesa, request);
        repository.atualizar(mesa);

        return mapper.toResponse(mesa);
    }

    public MesaTotalResponse calcularTotal(Long id) {
        buscarOuFalhar(id);
        List<Pedido> pedidos = pedidoRepository.listarPorMesa(id);
        double total = 0d;

        for (Pedido pedido : pedidos) {
            List<ItemPedido> itens = itemPedidoRepository.listarPorPedido(pedido.getId());
            for (ItemPedido item : itens) {
                total += item.getSubtotal();
            }
        }

        MesaTotalResponse response = new MesaTotalResponse();
        response.setMesaId(id);
        response.setTotal(total);
        return response;
    }

    private void atualizarObservacao(Mesa mesa, AtualizarStatusMesaRequest request) {
        if (request != null && request.getObservacao() != null) {
            mesa.setObservacao(request.getObservacao());
        }
    }

    private String validarNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new MesaNumeroObrigatorioException("Número da mesa é obrigatório");
        }
        return numero.trim();
    }

    private Mesa buscarOuFalhar(Long id) {
        Mesa mesa = repository.buscarPorId(id);
        if (mesa == null) {
            throw new MesaNaoEncontradaException("Mesa não encontrada com id: " + id);
        }
        return mesa;
    }
}
