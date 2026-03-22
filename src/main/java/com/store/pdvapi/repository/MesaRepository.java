package com.store.pdvapi.repository;

import com.store.pdvapi.model.Mesa;
import java.util.List;

public interface MesaRepository {

    void salvar(Mesa mesa);

    void atualizar(Mesa mesa);

    Mesa buscarPorId(Long id);

    Mesa buscarPorNumero(String numero);

    List<Mesa> listarTodos();
}
