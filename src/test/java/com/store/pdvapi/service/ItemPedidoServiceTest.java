package com.store.pdvapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.store.pdvapi.dto.itempedido.CriarItemPedidoRequest;
import com.store.pdvapi.dto.itempedido.ItemPedidoResponse;
import com.store.pdvapi.enumtype.StatusMesa;
import com.store.pdvapi.enumtype.StatusPedido;
import com.store.pdvapi.exception.PedidoNaoEncontradoException;
import com.store.pdvapi.exception.PedidoStatusInvalidoException;
import com.store.pdvapi.exception.ProdutoInativoException;
import com.store.pdvapi.model.Mesa;
import com.store.pdvapi.model.Pedido;
import com.store.pdvapi.model.Produto;
import com.store.pdvapi.repository.ItemPedidoRepository;
import com.store.pdvapi.repository.PedidoRepository;
import com.store.pdvapi.repository.ProdutoRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemPedidoServiceTest {

    private ItemPedidoService service;
    private RecordingItemPedidoRepository itemRepository;
    private RecordingPedidoRepository pedidoRepository;
    private RecordingProdutoRepository produtoRepository;

    @BeforeEach
    void setUp() {
        itemRepository = new RecordingItemPedidoRepository();
        pedidoRepository = new RecordingPedidoRepository();
        produtoRepository = new RecordingProdutoRepository();
        service = new ItemPedidoService(itemRepository, pedidoRepository, produtoRepository, new com.store.pdvapi.mapper.ItemPedidoMapper());
    }

    @Test
    void adicionar_quandoPedidoAbertoAdicionaItem() {
        Pedido pedido = new Pedido(1L, new Mesa(1L, "01", StatusMesa.OCUPADA, null), StatusPedido.ABERTO, null, null);
        pedidoRepository.seed(pedido);
        Produto produto = new Produto(1L, "Coca", 10.0, true);
        produtoRepository.seed(produto);

        CriarItemPedidoRequest request = new CriarItemPedidoRequest();
        request.setPedidoId(1L);
        request.setProdutoId(1L);
        request.setQuantidade(2);

        ItemPedidoResponse response = service.adicionar(request);

        assertEquals(1L, response.getPedidoId());
        assertEquals(1L, response.getProdutoId());
        assertEquals(2, response.getQuantidade());
        assertEquals(10.0, response.getPrecoUnitario());
        assertEquals(20.0, response.getSubtotal());
    }

    @Test
    void adicionar_quandoPedidoNaoEncontrado_lancaPedidoNaoEncontradoException() {
        CriarItemPedidoRequest request = new CriarItemPedidoRequest();
        request.setPedidoId(99L);
        request.setProdutoId(1L);
        request.setQuantidade(1);

        assertThrows(PedidoNaoEncontradoException.class, () -> service.adicionar(request));
    }

    @Test
    void adicionar_quandoPedidoFechado_lancaPedidoStatusInvalidoException() {
        Pedido pedido = new Pedido(2L, new Mesa(1L, "01", StatusMesa.OCUPADA, null), StatusPedido.FECHADO, null, null);
        pedidoRepository.seed(pedido);
        CriarItemPedidoRequest request = new CriarItemPedidoRequest();
        request.setPedidoId(2L);
        request.setProdutoId(1L);
        request.setQuantidade(1);

        assertThrows(PedidoStatusInvalidoException.class, () -> service.adicionar(request));
    }

    @Test
    void adicionar_quandoProdutoInativo_lancaProdutoInativoException() {
        Pedido pedido = new Pedido(3L, new Mesa(1L, "01", StatusMesa.OCUPADA, null), StatusPedido.ABERTO, null, null);
        pedidoRepository.seed(pedido);
        Produto produto = new Produto(2L, "Fanta", 8.0, false);
        produtoRepository.seed(produto);
        CriarItemPedidoRequest request = new CriarItemPedidoRequest();
        request.setPedidoId(3L);
        request.setProdutoId(2L);
        request.setQuantidade(1);

        assertThrows(ProdutoInativoException.class, () -> service.adicionar(request));
    }

    @Test
    void listarPorPedido_devolveItens() {
        Pedido pedido = new Pedido(4L, new Mesa(1L, "01", StatusMesa.OCUPADA, null), StatusPedido.ABERTO, null, null);
        pedidoRepository.seed(pedido);
        Produto produto = new Produto(3L, "Suco", 5.0, true);
        produtoRepository.seed(produto);
        CriarItemPedidoRequest request = new CriarItemPedidoRequest();
        request.setPedidoId(4L);
        request.setProdutoId(3L);
        request.setQuantidade(2);
        service.adicionar(request);

        List<ItemPedidoResponse> responses = service.listarPorPedido(4L);

        assertEquals(1, responses.size());
    }

    private static class RecordingItemPedidoRepository implements ItemPedidoRepository {

        private final Map<Long, com.store.pdvapi.model.ItemPedido> store = new LinkedHashMap<>();
        private long nextId = 1;

        @Override
        public void salvar(com.store.pdvapi.model.ItemPedido item) {
            if (item.getId() == null) {
                item.setId(nextId++);
            }
            store.put(item.getId(), item);
        }

        @Override
        public void atualizar(com.store.pdvapi.model.ItemPedido item) {
            store.put(item.getId(), item);
        }

        @Override
        public com.store.pdvapi.model.ItemPedido buscarPorId(Long id) {
            return store.get(id);
        }

        @Override
        public List<com.store.pdvapi.model.ItemPedido> listarPorPedido(Long pedidoId) {
            List<com.store.pdvapi.model.ItemPedido> list = new ArrayList<>();
            for (com.store.pdvapi.model.ItemPedido item : store.values()) {
                if (item.getPedido() != null && pedidoId.equals(item.getPedido().getId())) {
                    list.add(item);
                }
            }
            return list;
        }
    }

    private static class RecordingPedidoRepository implements PedidoRepository {

        private final Map<Long, Pedido> store = new HashMap<>();

        void seed(Pedido pedido) {
            store.put(pedido.getId(), pedido);
        }

        @Override
        public void salvar(Pedido pedido) {
            store.put(pedido.getId(), pedido);
        }

        @Override
        public void atualizar(Pedido pedido) {
            store.put(pedido.getId(), pedido);
        }

        @Override
        public Pedido buscarPorId(Long id) {
            return store.get(id);
        }

        @Override
        public List<Pedido> listarPorMesa(Long mesaId) {
            List<Pedido> list = new ArrayList<>();
            for (Pedido pedido : store.values()) {
                if (pedido.getMesa() != null && mesaId.equals(pedido.getMesa().getId())) {
                    list.add(pedido);
                }
            }
            return list;
        }
    }

    private static class RecordingProdutoRepository implements ProdutoRepository {

        private final Map<Long, Produto> store = new HashMap<>();

        void seed(Produto produto) {
            store.put(produto.getId(), produto);
        }

        @Override
        public Produto buscarPorId(Long id) {
            return store.get(id);
        }

        @Override
        public void salvar(Produto produto) {
            store.put(produto.getId(), produto);
        }

        @Override
        public void atualizar(Produto produto) {
            store.put(produto.getId(), produto);
        }

        @Override
        public List<Produto> listarTodos() {
            return new ArrayList<>(store.values());
        }
    }
}
