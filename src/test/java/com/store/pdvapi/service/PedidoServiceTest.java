package com.store.pdvapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.store.pdvapi.dto.pedido.CriarPedidoRequest;
import com.store.pdvapi.dto.pedido.PedidoResponse;
import com.store.pdvapi.enumtype.StatusMesa;
import com.store.pdvapi.enumtype.StatusPedido;
import com.store.pdvapi.model.Mesa;
import com.store.pdvapi.model.Pedido;
import com.store.pdvapi.repository.MesaRepository;
import com.store.pdvapi.repository.PedidoRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PedidoServiceTest {

    private PedidoService service;
    private RecordingPedidoRepository pedidoRepository;
    private RecordingMesaRepository mesaRepository;

    @BeforeEach
    void setUp() {
        pedidoRepository = new RecordingPedidoRepository();
        mesaRepository = new RecordingMesaRepository();
        service = new PedidoService(pedidoRepository, mesaRepository, new com.store.pdvapi.mapper.PedidoMapper());
    }

    @Test
    void criar_quandoMesaOcupada_salvaPedidoAberto() {
        Mesa mesa = new Mesa(1L, "01", StatusMesa.OCUPADA, null);
        mesaRepository.seed(mesa);

        CriarPedidoRequest request = new CriarPedidoRequest();
        request.setMesaId(1L);

        PedidoResponse resposta = service.criar(request);

        assertEquals(StatusPedido.ABERTO, resposta.getStatus());
        assertEquals(1L, resposta.getMesaId());
        assertNotNull(resposta.getDataCriacao());
        assertNotNull(resposta.getHoraCriacao());
        assertEquals(StatusPedido.ABERTO, pedidoRepository.getLastSaved().getStatus());
    }

    @Test
    void criar_quandoMesaNaoExiste_lancaRuntimeException() {
        CriarPedidoRequest request = new CriarPedidoRequest();
        request.setMesaId(99L);

        assertThrows(RuntimeException.class, () -> service.criar(request));
    }

    @Test
    void criar_quandoMesaNaoOcupada_lancaRuntimeException() {
        Mesa mesaLivre = new Mesa(2L, "02", StatusMesa.LIVRE, null);
        mesaRepository.seed(mesaLivre);

        CriarPedidoRequest request = new CriarPedidoRequest();
        request.setMesaId(2L);

        assertThrows(RuntimeException.class, () -> service.criar(request));
    }

    @Test
    void fechar_quandoPedidoAberto_atualizaStatusFechado() {
        Pedido pedido = new Pedido(1L, new Mesa(1L, "01", StatusMesa.OCUPADA, null), StatusPedido.ABERTO, LocalDate.now(), LocalTime.now());
        pedidoRepository.seed(pedido);

        PedidoResponse resposta = service.fechar(1L);

        assertEquals(StatusPedido.FECHADO, resposta.getStatus());
        assertEquals(StatusPedido.FECHADO, pedidoRepository.getStored(1L).getStatus());
    }

    @Test
    void fechar_quandoPedidoNaoEncontrado_lancaRuntimeException() {
        assertThrows(RuntimeException.class, () -> service.fechar(7L));
    }

    @Test
    void fechar_quandoJaFechado_lancaRuntimeException() {
        Pedido pedido = new Pedido(2L, new Mesa(1L, "01", StatusMesa.OCUPADA, null), StatusPedido.FECHADO, LocalDate.now(), LocalTime.now());
        pedidoRepository.seed(pedido);

        assertThrows(RuntimeException.class, () -> service.fechar(2L));
    }

    private static class RecordingPedidoRepository implements PedidoRepository {

        private final Map<Long, Pedido> store = new LinkedHashMap<>();
        private long nextId = 1;

        void seed(Pedido pedido) {
            store.put(pedido.getId(), pedido);
            nextId = Math.max(nextId, pedido.getId() + 1);
        }

        Pedido getLastSaved() {
            return store.get(nextId - 1);
        }

        Pedido getStored(Long id) {
            return store.get(id);
        }

        @Override
        public void salvar(Pedido pedido) {
            if (pedido.getId() == null) {
                pedido.setId(nextId++);
            }
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

    private static class RecordingMesaRepository implements MesaRepository {

        private final Map<Long, Mesa> store = new HashMap<>();

        void seed(Mesa mesa) {
            store.put(mesa.getId(), mesa);
        }

        @Override
        public Mesa buscarPorId(Long id) {
            return store.get(id);
        }

        @Override
        public Mesa buscarPorNumero(String numero) {
            return store.values().stream()
                    .filter(mesa -> numero.equals(mesa.getNumero()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<Mesa> listarTodos() {
            return new ArrayList<>(store.values());
        }

        @Override
        public void salvar(Mesa mesa) {
            store.put(mesa.getId(), mesa);
        }

        @Override
        public void atualizar(Mesa mesa) {
            store.put(mesa.getId(), mesa);
        }
    }
}
