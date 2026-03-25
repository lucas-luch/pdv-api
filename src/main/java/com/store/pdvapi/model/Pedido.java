package com.store.pdvapi.model;

import com.store.pdvapi.enumtype.StatusPedido;
import java.time.LocalDate;
import java.time.LocalTime;

public class Pedido {
    private Long id;
    private Mesa mesa;
    private StatusPedido status;
    private LocalDate dataCriacao;
    private LocalTime horaCriacao;

    public Pedido() {
    }

    public Pedido(Long id, Mesa mesa, StatusPedido status, LocalDate dataCriacao, LocalTime horaCriacao) {
        this.id = id;
        this.mesa = mesa;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.horaCriacao = horaCriacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalTime getHoraCriacao() {
        return horaCriacao;
    }

    public void setHoraCriacao(LocalTime horaCriacao) {
        this.horaCriacao = horaCriacao;
    }
}
