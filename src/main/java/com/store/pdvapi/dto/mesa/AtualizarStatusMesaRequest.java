package com.store.pdvapi.dto.mesa;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AtualizarStatusMesaRequest", description = "Observação registrada ao alterar o status da mesa.")
public class AtualizarStatusMesaRequest {

    @Schema(description = "Comentário opcional sobre o motivo da mudança de status", example = "Mesa liberada após pagamento")
    private String observacao;

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
