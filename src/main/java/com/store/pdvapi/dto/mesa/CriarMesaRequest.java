package com.store.pdvapi.dto.mesa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "CriarMesaRequest", description = "Dados usados para registrar uma nova mesa.")
public class CriarMesaRequest {

    @Schema(description = "Número único da mesa", example = "12")
    @NotBlank(message = "Número da mesa é obrigatório")
    private String numero;

    @Schema(description = "Observação opcional para controle da mesa", example = "Próxima à janela")
    private String observacao;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
