package com.store.pdvapi.dto.mesa;

import com.store.pdvapi.enumtype.StatusMesa;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MesaResponse", description = "Dados retornados sobre uma mesa, incluindo número e status.")
public class MesaResponse {

    @Schema(description = "Identificador da mesa", example = "1")
    private Long id;

    @Schema(description = "Número da mesa", example = "10")
    private String numero;

    @Schema(description = "Status atual da mesa", example = "LIVRE")
    private StatusMesa status;

    @Schema(description = "Observação final registrada na mesa", example = "Cliente pediu guarda-volume")
    private String observacao;

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
