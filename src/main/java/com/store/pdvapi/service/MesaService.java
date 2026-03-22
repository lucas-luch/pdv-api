package com.store.pdvapi.service;

import com.store.pdvapi.dto.mesa.CriarMesaRequest;
import com.store.pdvapi.dto.mesa.MesaResponse;
import com.store.pdvapi.exception.MesaNaoEncontradaException;
import com.store.pdvapi.mapper.MesaMapper;
import com.store.pdvapi.model.Mesa;
import com.store.pdvapi.repository.MesaRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MesaService {

    private final MesaRepository repository;
    private final MesaMapper mapper;

    public MesaService(MesaRepository repository, MesaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public MesaResponse criar(CriarMesaRequest request) {
        Mesa mesa = mapper.toEntity(request);
        repository.salvar(mesa);
        return mapper.toResponse(mesa);
    }

    public MesaResponse buscarPorId(Long id) {
        Mesa mesa = buscarOuFalhar(id);
        return mapper.toResponse(mesa);
    }

    public List<MesaResponse> listar() {
        List<Mesa> mesas = repository.listarTodos();
        List<MesaResponse> responses = new ArrayList<>();

        for (Mesa mesa : mesas) {
            responses.add(mapper.toResponse(mesa));
        }

        return responses;
    }

    private Mesa buscarOuFalhar(Long id) {
        Mesa mesa = repository.buscarPorId(id);
        if (mesa == null) {
            throw new MesaNaoEncontradaException("Mesa não encontrada com id: " + id);
        }
        return mesa;
    }
}
