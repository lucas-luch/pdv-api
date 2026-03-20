package com.store.pdvapi.repository;
import org.springframework.stereotype.Repository;
import com.store.pdvapi.model.Produto;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class ProdutoRepositoryImpl implements ProdutoRepository {

    private final DataSource dataSource;

    public ProdutoRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void salvar(Produto produto) {
        String sql = "INSERT INTO produto (nome, preco, ativo) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setBoolean(3, produto.isAtivo());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produto.setId(generatedKeys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto", e);
        }
    }

    @Override
    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, preco = ?, ativo = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setBoolean(3, produto.isAtivo());
            stmt.setLong(4, produto.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto", e);
        }
    }

    @Override
    public Produto buscarPorId(Long id) {
        String sql = "SELECT id, nome, preco, ativo FROM produto WHERE id = ?";

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
            throw new RuntimeException("Erro ao buscar produto", e);
        }
    }

    @Override
    public List<Produto> listarTodos() {
        String sql = "SELECT id, nome, preco, ativo FROM produto";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produtos.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos", e);
        }

        return produtos;
    }

    private Produto mapRow(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getLong("id"));
        produto.setNome(rs.getString("nome"));
        produto.setPreco(rs.getDouble("preco"));
        produto.setAtivo(rs.getBoolean("ativo"));
        return produto;
    }

}
