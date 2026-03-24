package com.store.pdvapi.repository;

import java.util.List;

import com.store.pdvapi.model.Mesa;

public interface MesaRepository {

    void salvar(Mesa mesa);

    void atualizar(Mesa mesa);

    Mesa buscarPorId(Long id);

    Mesa buscarPorNumero(String numero);

    List<Mesa> listarTodos();
}
