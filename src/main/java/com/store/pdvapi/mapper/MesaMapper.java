package com.store.pdvapi.mapper;

import org.springframework.stereotype.Component;

import com.store.pdvapi.dto.mesa.CriarMesaRequest;
import com.store.pdvapi.dto.mesa.MesaResponse;
import com.store.pdvapi.enumtype.StatusMesa;
import com.store.pdvapi.model.Mesa;

@Component
public class MesaMapper {

    public Mesa toEntity(CriarMesaRequest request) {
        Mesa mesa = new Mesa();
        mesa.setNumero(request.getNumero());
        mesa.setObservacao(request.getObservacao());
        mesa.setStatus(StatusMesa.LIVRE);
        return mesa;
    }

    public MesaResponse toResponse(Mesa mesa) {
        MesaResponse response = new MesaResponse();
        response.setId(mesa.getId());
        response.setNumero(mesa.getNumero());
        response.setStatus(mesa.getStatus());
        response.setObservacao(mesa.getObservacao());
        return response;
    }
}
