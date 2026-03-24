package com.store.pdvapi.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.store.pdvapi.enumtype.StatusMesa;
import com.store.pdvapi.model.Mesa;

@Repository
public class MesaRepositoryImpl implements MesaRepository {

    private final DataSource dataSource;

    public MesaRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void salvar(Mesa mesa) {
        String sql = "INSERT INTO mesa (numero, status, observacao) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, mesa.getNumero());
            stmt.setString(2, mesa.getStatus().name());
            stmt.setString(3, mesa.getObservacao());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mesa.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar mesa", e);
        }
    }

    @Override
    public void atualizar(Mesa mesa) {
        String sql = "UPDATE mesa SET numero = ?, status = ?, observacao = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mesa.getNumero());
            stmt.setString(2, mesa.getStatus().name());
            stmt.setString(3, mesa.getObservacao());
            stmt.setLong(4, mesa.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar mesa", e);
        }
    }

    @Override
    public Mesa buscarPorId(Long id) {
        String sql = "SELECT id, numero, status, observacao FROM mesa WHERE id = ?";

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
            throw new RuntimeException("Erro ao buscar mesa", e);
        }
    }

    @Override
    public Mesa buscarPorNumero(String numero) {
        String sql = "SELECT id, numero, status, observacao FROM mesa WHERE numero = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numero);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar mesa por numero", e);
        }
    }

    @Override
    public List<Mesa> listarTodos() {
        String sql = "SELECT id, numero, status, observacao FROM mesa";
        List<Mesa> mesas = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                mesas.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar mesas", e);
        }

        return mesas;
    }

    private Mesa mapRow(ResultSet rs) throws SQLException {
        Mesa mesa = new Mesa();
        mesa.setId(rs.getLong("id"));
        mesa.setNumero(rs.getString("numero"));
        mesa.setStatus(StatusMesa.valueOf(rs.getString("status")));
        mesa.setObservacao(rs.getString("observacao"));
        return mesa;
    }
}
