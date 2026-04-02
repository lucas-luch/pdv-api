package com.store.pdvapi.service;

import com.store.pdvapi.dto.itempedido.CriarItemPedidoRequest;
import com.store.pdvapi.dto.itempedido.ItemPedidoResponse;
import com.store.pdvapi.enumtype.StatusPedido;
import com.store.pdvapi.exception.PedidoNaoEncontradoException;
import com.store.pdvapi.exception.PedidoStatusInvalidoException;
import com.store.pdvapi.exception.ProdutoInativoException;
import com.store.pdvapi.exception.ProdutoNaoEncontradoException;
import com.store.pdvapi.mapper.ItemPedidoMapper;
import com.store.pdvapi.model.ItemPedido;
import com.store.pdvapi.model.Pedido;
import com.store.pdvapi.model.Produto;
import com.store.pdvapi.repository.ItemPedidoRepository;
import com.store.pdvapi.repository.PedidoRepository;
import com.store.pdvapi.repository.ProdutoRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ItemPedidoService {

    private final ItemPedidoRepository repository;
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoMapper mapper;

    public ItemPedidoService(ItemPedidoRepository repository,
                             PedidoRepository pedidoRepository,
                             ProdutoRepository produtoRepository,
                             ItemPedidoMapper mapper) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.mapper = mapper;
    }

    public ItemPedidoResponse adicionar(CriarItemPedidoRequest request) {
        Pedido pedido = buscarPedido(request.getPedidoId());
        validarPedidoAbertoParaAdicionarItem(pedido);

        Produto produto = produtoRepository.buscarPorId(request.getProdutoId());
        validarProdutoParaAdicionarItem(produto, request.getProdutoId());

        ItemPedido item = mapper.toEntity(request);
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setPrecoUnitario(produto.getPreco());
        item.setSubtotal(produto.getPreco() * request.getQuantidade());

        repository.salvar(item);
        return mapper.toResponse(item);
    }

    public List<ItemPedidoResponse> listarPorPedido(Long pedidoId) {
        List<ItemPedido> itens = repository.listarPorPedido(pedidoId);
        List<ItemPedidoResponse> responses = new ArrayList<>();
        for (ItemPedido item : itens) {
            responses.add(mapper.toResponse(item));
        }
        return responses;
    }

    private Pedido buscarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (pedido == null) {
            throw new PedidoNaoEncontradoException("Pedido não encontrado com id: " + pedidoId);
        }
        return pedido;
    }

    private void validarPedidoAbertoParaAdicionarItem(Pedido pedido) {
        if (pedido.getStatus() != StatusPedido.ABERTO) {
            throw new PedidoStatusInvalidoException("Só é possível adicionar itens a pedidos abertos");
        }
    }

    private void validarProdutoParaAdicionarItem(Produto produto, Long produtoId) {
        if (produto == null) {
            throw new ProdutoNaoEncontradoException("Produto não encontrado com id: " + produtoId);
        }

        if (!produto.isAtivo()) {
            throw new ProdutoInativoException("Produto inativo não pode ser adicionado ao pedido");
        }
    }
}
