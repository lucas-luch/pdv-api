package com.store.pdvapi.repository;

import com.store.pdvapi.enumtype.StatusPedido;
import com.store.pdvapi.model.Mesa;
import com.store.pdvapi.model.Pedido;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class PedidoRepositoryImpl implements PedidoRepository {

    private final DataSource dataSource;

    public PedidoRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void salvar(Pedido pedido) {
        String sql = "INSERT INTO pedido (mesa_id, status, data_criacao, hora_criacao) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, pedido.getMesa().getId());
            stmt.setString(2, pedido.getStatus().name());
            stmt.setDate(3, toSqlDate(pedido.getDataCriacao()));
            stmt.setTime(4, toSqlTime(pedido.getHoraCriacao()));

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pedido.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar pedido", e);
        }
    }

    @Override
    public void atualizar(Pedido pedido) {
        String sql = "UPDATE pedido SET mesa_id = ?, status = ?, data_criacao = ?, hora_criacao = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pedido.getMesa().getId());
            stmt.setString(2, pedido.getStatus().name());
            stmt.setDate(3, toSqlDate(pedido.getDataCriacao()));
            stmt.setTime(4, toSqlTime(pedido.getHoraCriacao()));
            stmt.setLong(5, pedido.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pedido", e);
        }
    }

    @Override
    public Pedido buscarPorId(Long id) {
        String sql = "SELECT id, mesa_id, status, data_criacao, hora_criacao FROM pedido WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedido", e);
        }
    }

    @Override
    public List<Pedido> listarPorMesa(Long mesaId) {
        String sql = "SELECT id, mesa_id, status, data_criacao, hora_criacao FROM pedido WHERE mesa_id = ?";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, mesaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos", e);
        }

        return pedidos;
    }

    private Pedido mapRow(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        Mesa mesa = new Mesa();
        mesa.setId(rs.getLong("mesa_id"));
        pedido.setId(rs.getLong("id"));
        pedido.setMesa(mesa);
        pedido.setStatus(StatusPedido.valueOf(rs.getString("status")));
        pedido.setDataCriacao(toLocalDate(rs.getDate("data_criacao")));
        pedido.setHoraCriacao(toLocalTime(rs.getTime("hora_criacao")));
        return pedido;
    }

    private Date toSqlDate(LocalDate value) {
        return value != null ? Date.valueOf(value) : null;
    }

    private Time toSqlTime(LocalTime value) {
        return value != null ? Time.valueOf(value) : null;
    }

    private LocalDate toLocalDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    private LocalTime toLocalTime(Time time) {
        return time != null ? time.toLocalTime() : null;
    }
}
