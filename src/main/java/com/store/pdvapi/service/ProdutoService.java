package com.store.pdvapi.service;
import com.store.pdvapi.repository.ProdutoRepository;
import com.store.pdvapi.dto.produto.AtualizarProdutoRequest;
import com.store.pdvapi.dto.produto.CriarProdutoRequest;
import com.store.pdvapi.dto.produto.ProdutoResponse;
import com.store.pdvapi.exception.ProdutoNaoEncontradoException;
import com.store.pdvapi.mapper.ProdutoMapper;
import com.store.pdvapi.model.Produto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final ProdutoMapper mapper;

    public ProdutoService(ProdutoRepository repository, ProdutoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ProdutoResponse criar(CriarProdutoRequest request) {

        Produto produto = mapper.toEntity(request);
        repository.salvar(produto);
        return mapper.toResponse(produto);
    }

    public ProdutoResponse buscarPorId(Long id) {
        Produto produto = buscarOuFalhar(id);
        return mapper.toResponse(produto);
    }

    public List<ProdutoResponse> listar() {
        List<Produto> produtos = repository.listarTodos();
        List<ProdutoResponse> responses = new ArrayList<>();

        for (Produto produto : produtos) {
            responses.add(mapper.toResponse(produto));
        }

        return responses;
    }

    public ProdutoResponse atualizar(Long id, AtualizarProdutoRequest request) {
        Produto produto = buscarOuFalhar(id);

        produto.setNome(request.getNome());
        produto.setPreco(request.getPreco());
        produto.setAtivo(request.isAtivo());

        repository.atualizar(produto);

        return mapper.toResponse(produto);
    }

    public ProdutoResponse ativar(Long id) {
        Produto produto = buscarOuFalhar(id);

        produto.setAtivo(true);
        repository.atualizar(produto);

        return mapper.toResponse(produto);
    }

    public ProdutoResponse inativar(Long id) {
        Produto produto = buscarOuFalhar(id);

        produto.setAtivo(false);
        repository.atualizar(produto);

        return mapper.toResponse(produto);
    }

    private Produto buscarOuFalhar(Long id) {
        Produto produto = repository.buscarPorId(id);
        if (produto == null) {
            throw new ProdutoNaoEncontradoException("Produto não encontrado com id: " + id);
        }
        return produto;
    }

}
