package com.store.pdvapi.dto.pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(name = "CriarPedidoRequest", description = "Payload para abrir um pedido vinculando-o a uma mesa ocupada.")
public class CriarPedidoRequest {

    @Schema(description = "ID da mesa para a qual o pedido será criado", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Mesa é obrigatória")
    @Positive(message = "ID da mesa deve ser maior que zero")
    private Long mesaId;

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }
}
