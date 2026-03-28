package com.store.pdvapi.dto.pedido;

import com.store.pdvapi.enumtype.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(name = "PedidoResponse", description = "Dados retornados para cada pedido aberto ou fechado.")
public class PedidoResponse {

    @Schema(description = "Identificador do pedido", example = "10")
    private Long id;

    @Schema(description = "ID da mesa associada ao pedido", example = "4")
    private Long mesaId;

    @Schema(description = "Status atual do pedido", example = "ABERTO")
    private StatusPedido status;

    @Schema(description = "Data de criação do pedido", example = "2026-03-28")
    private LocalDate dataCriacao;

    @Schema(description = "Hora de abertura do pedido", example = "18:45:00")
    private LocalTime horaCriacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
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
