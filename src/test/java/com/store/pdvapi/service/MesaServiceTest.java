package com.store.pdvapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.store.pdvapi.dto.mesa.AtualizarStatusMesaRequest;
import com.store.pdvapi.dto.mesa.CriarMesaRequest;
import com.store.pdvapi.dto.mesa.MesaTotalResponse;
import com.store.pdvapi.enumtype.StatusMesa;
import com.store.pdvapi.enumtype.StatusPedido;
import com.store.pdvapi.exception.MesaNaoEncontradaException;
import com.store.pdvapi.exception.MesaStatusInvalidoException;
import com.store.pdvapi.mapper.MesaMapper;
import com.store.pdvapi.model.ItemPedido;
import com.store.pdvapi.model.Mesa;
import com.store.pdvapi.model.Pedido;
import com.store.pdvapi.model.Produto;
import com.store.pdvapi.repository.ItemPedidoRepository;
import com.store.pdvapi.repository.MesaRepository;
import com.store.pdvapi.repository.PedidoRepository;

class MesaServiceTest {

    private MesaService service;
    private RecordingMesaRepository repository;
    private RecordingPedidoRepository pedidoRepository;
    private RecordingItemPedidoRepository itemPedidoRepository;

    @BeforeEach
    void setUp() {
        repository = new RecordingMesaRepository();
        pedidoRepository = new RecordingPedidoRepository();
        itemPedidoRepository = new RecordingItemPedidoRepository();
        service = new MesaService(repository, new MesaMapper(), pedidoRepository, itemPedidoRepository);
    }

    @Test
    void criar_deveValidarNumeroEFocarStatusLivre() {
        CriarMesaRequest request = new CriarMesaRequest();
        request.setNumero(" 05 ");
        request.setObservacao("Janela");

        var resposta = service.criar(request);

        assertEquals("05", resposta.getNumero());
        assertEquals(StatusMesa.LIVRE, resposta.getStatus());
        assertEquals("Janela", resposta.getObservacao());
        assertEquals(StatusMesa.LIVRE, repository.getLastSaved().getStatus());
    }

    @Test
    void criar_quandoNumeroDuplicado_lancaRuntime() {
        Mesa existente = new Mesa(1L, "10", StatusMesa.FECHADA, "Ocupada");
        repository.seed(existente);

        CriarMesaRequest request = new CriarMesaRequest();
        request.setNumero("10");

        assertThrows(RuntimeException.class, () -> service.criar(request));
    }

    @Test
    void abrir_quandoLivre_atualizaStatusEObservacao() {
        Mesa existente = new Mesa(2L, "20", StatusMesa.LIVRE, null);
        repository.seed(existente);

        AtualizarStatusMesaRequest request = new AtualizarStatusMesaRequest();
        request.setObservacao("Cliente chegou");

        var resposta = service.abrir(2L, request);

        assertEquals(StatusMesa.OCUPADA, resposta.getStatus());
        assertEquals("Cliente chegou", resposta.getObservacao());
        assertEquals(StatusMesa.OCUPADA, repository.getStored(2L).getStatus());
    }

    @Test
    void abrir_quandoNaoLivre_lancaMesaStatusInvalido() {
        Mesa ocupada = new Mesa(3L, "30", StatusMesa.OCUPADA, null);
        repository.seed(ocupada);

        assertThrows(MesaStatusInvalidoException.class, () -> service.abrir(3L, null));
    }

    @Test
    void fechar_quandoOcupada_mudaParaFechada() {
        Mesa ocupada = new Mesa(4L, "40", StatusMesa.OCUPADA, null);
        repository.seed(ocupada);

        var resposta = service.fechar(4L, null);

        assertEquals(StatusMesa.FECHADA, resposta.getStatus());
        assertEquals(StatusMesa.FECHADA, repository.getStored(4L).getStatus());
    }

    @Test
    void fechar_quandoNaoOcupada_lancaMesaStatusInvalido() {
        Mesa livre = new Mesa(5L, "50", StatusMesa.LIVRE, null);
        repository.seed(livre);

        assertThrows(MesaStatusInvalidoException.class, () -> service.fechar(5L, null));
    }

    @Test
    void liberar_quandoNaoLivre_restauraStatusLivre() {
        Mesa fechada = new Mesa(6L, "60", StatusMesa.FECHADA, "Limpeza");
        repository.seed(fechada);

        var resposta = service.liberar(6L, null);

        assertEquals(StatusMesa.LIVRE, resposta.getStatus());
        assertEquals(StatusMesa.LIVRE, repository.getStored(6L).getStatus());
    }

    @Test
    void liberar_quandoJaLivre_lancaMesaStatusInvalido() {
        Mesa livre = new Mesa(7L, "70", StatusMesa.LIVRE, null);
        repository.seed(livre);

        assertThrows(MesaStatusInvalidoException.class, () -> service.liberar(7L, null));
    }

    @Test
    void buscarPorId_quandoNaoExistir_lancaMesaNaoEncontrada() {
        assertThrows(MesaNaoEncontradaException.class, () -> service.buscarPorId(999L));
    }

    @Test
    void calcularTotal_deveSomarItensDoPedido() {
        Mesa mesa = new Mesa(20L, "20", StatusMesa.FECHADA, null);
        repository.seed(mesa);

        LocalDate data = LocalDate.now();
        LocalTime hora = LocalTime.now();
        Pedido pedido = new Pedido(1L, mesa, StatusPedido.ABERTO, data, hora);
        pedidoRepository.seed(pedido);

        Produto produtoA = new Produto(1L, "Cafe", 10.0, true);
        Produto produtoB = new Produto(2L, "Bolo", 15.0, true);
        ItemPedido itemA = new ItemPedido(1L, pedido, produtoA, 1, 10.0, 10.0);
        ItemPedido itemB = new ItemPedido(2L, pedido, produtoB, 1, 15.0, 15.0);
        itemPedidoRepository.seed(itemA);
        itemPedidoRepository.seed(itemB);

        MesaTotalResponse total = service.calcularTotal(mesa.getId());

        assertEquals(mesa.getId(), total.getMesaId());
        assertEquals(25.0, total.getTotal());
    }

    private static class RecordingMesaRepository implements MesaRepository {

        private final Map<Long, Mesa> store = new LinkedHashMap<>();
        private long nextId = 1;
        private Mesa lastSaved;

        Mesa getLastSaved() {
            return lastSaved;
        }

        Mesa getStored(Long id) {
            return store.get(id);
        }

        void seed(Mesa mesa) {
            store.put(mesa.getId(), mesa);
            nextId = Math.max(nextId, mesa.getId() + 1);
        }

        @Override
        public void salvar(Mesa mesa) {
            if (mesa.getId() == null) {
                mesa.setId(nextId++);
            }
            store.put(mesa.getId(), mesa);
            lastSaved = mesa;
        }

        @Override
        public void atualizar(Mesa mesa) {
            store.put(mesa.getId(), mesa);
        }

        @Override
        public Mesa buscarPorId(Long id) {
            return store.get(id);
        }

        @Override
        public Mesa buscarPorNumero(String numero) {
            return store.values().stream()
                    .filter(mesa -> mesa.getNumero().equals(numero))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<Mesa> listarTodos() {
            return new ArrayList<>(store.values());
        }
    }

    private static class RecordingPedidoRepository implements PedidoRepository {

        private final Map<Long, Pedido> store = new LinkedHashMap<>();

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
            List<Pedido> pedidos = new ArrayList<>();
            for (Pedido pedido : store.values()) {
                if (pedido.getMesa() != null && mesaId.equals(pedido.getMesa().getId())) {
                    pedidos.add(pedido);
                }
            }
            return pedidos;
        }
    }

    private static class RecordingItemPedidoRepository implements ItemPedidoRepository {

        private final Map<Long, ItemPedido> store = new LinkedHashMap<>();

        void seed(ItemPedido item) {
            store.put(item.getId(), item);
        }

        @Override
        public void salvar(ItemPedido item) {
            store.put(item.getId(), item);
        }

        @Override
        public void atualizar(ItemPedido item) {
            store.put(item.getId(), item);
        }

        @Override
        public ItemPedido buscarPorId(Long id) {
            return store.get(id);
        }

        @Override
        public List<ItemPedido> listarPorPedido(Long pedidoId) {
            List<ItemPedido> itens = new ArrayList<>();
            for (ItemPedido item : store.values()) {
                if (item.getPedido() != null && pedidoId.equals(item.getPedido().getId())) {
                    itens.add(item);
                }
            }
            return itens;
        }
    }
}
