package com.store.pdvapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.store.pdvapi.dto.mesa.AtualizarStatusMesaRequest;
import com.store.pdvapi.dto.mesa.CriarMesaRequest;
import com.store.pdvapi.enumtype.StatusMesa;
import com.store.pdvapi.exception.MesaNaoEncontradaException;
import com.store.pdvapi.exception.MesaStatusInvalidoException;
import com.store.pdvapi.mapper.MesaMapper;
import com.store.pdvapi.model.ItemPedido;
import com.store.pdvapi.model.Mesa;
import com.store.pdvapi.model.Pedido;
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

        @Override
        public void salvar(Pedido pedido) {
            // unused
        }

        @Override
        public void atualizar(Pedido pedido) {
            // unused
        }

        @Override
        public Pedido buscarPorId(Long id) {
            return null;
        }

        @Override
        public List<Pedido> listarPorMesa(Long mesaId) {
            return new ArrayList<>();
        }
    }

    private static class RecordingItemPedidoRepository implements ItemPedidoRepository {

        @Override
        public void salvar(ItemPedido item) {
            // unused
        }

        @Override
        public void atualizar(ItemPedido item) {
            // unused
        }

        @Override
        public ItemPedido buscarPorId(Long id) {
            return null;
        }

        @Override
        public List<ItemPedido> listarPorPedido(Long pedidoId) {
            return new ArrayList<>();
        }
    }
}
