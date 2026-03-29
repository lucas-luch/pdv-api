package com.store.pdvapi.repository;

import com.store.pdvapi.model.ItemPedido;
import com.store.pdvapi.model.Pedido;
import com.store.pdvapi.model.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class ItemPedidoRepositoryImpl implements ItemPedidoRepository {

    private final DataSource dataSource;

    public ItemPedidoRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void salvar(ItemPedido item) {
        String sql = "INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, item.getPedido().getId());
            stmt.setLong(2, item.getProduto().getId());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getPrecoUnitario());
            stmt.setDouble(5, item.getSubtotal());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar item do pedido", e);
        }
    }

    @Override
    public void atualizar(ItemPedido item) {
        String sql = "UPDATE item_pedido SET quantidade = ?, preco_unitario = ?, subtotal = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getQuantidade());
            stmt.setDouble(2, item.getPrecoUnitario());
            stmt.setDouble(3, item.getSubtotal());
            stmt.setLong(4, item.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item do pedido", e);
        }
    }

    @Override
    public ItemPedido buscarPorId(Long id) {
        String sql = "SELECT id, pedido_id, produto_id, quantidade, preco_unitario, subtotal FROM item_pedido WHERE id = ?";

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
            throw new RuntimeException("Erro ao buscar item do pedido", e);
        }
    }

    @Override
    public List<ItemPedido> listarPorPedido(Long pedidoId) {
        String sql = "SELECT id, pedido_id, produto_id, quantidade, preco_unitario, subtotal FROM item_pedido WHERE pedido_id = ?";
        List<ItemPedido> itens = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pedidoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    itens.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens do pedido", e);
        }

        return itens;
    }

    private ItemPedido mapRow(ResultSet rs) throws SQLException {
        ItemPedido item = new ItemPedido();
        item.setId(rs.getLong("id"));
        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("pedido_id"));
        item.setPedido(pedido);
        Produto produto = new Produto();
        produto.setId(rs.getLong("produto_id"));
        item.setProduto(produto);
        item.setQuantidade(rs.getInt("quantidade"));
        item.setPrecoUnitario(rs.getDouble("preco_unitario"));
        item.setSubtotal(rs.getDouble("subtotal"));
        return item;
    }
}
