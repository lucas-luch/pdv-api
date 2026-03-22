package com.store.pdvapi.model;

import com.store.pdvapi.enumtype.StatusMesa;

public class Mesa {
    private Long id;
    private String numero;
    private StatusMesa status;
    private String observacao;

    public Mesa() {
    }

    public Mesa(Long id, String numero, StatusMesa status, String observacao) {
        this.id = id;
        this.numero = numero;
        this.status = status;
        this.observacao = observacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public StatusMesa getStatus() {
        return status;
    }

    public void setStatus(StatusMesa status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
