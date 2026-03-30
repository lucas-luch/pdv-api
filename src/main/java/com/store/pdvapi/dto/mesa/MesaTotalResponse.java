package com.store.pdvapi.dto.mesa;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MesaTotalResponse", description = "Valor total calculado para todos os pedidos ligados à mesa.")
public class MesaTotalResponse {

    @Schema(description = "Identificador da mesa", example = "1")
    private Long mesaId;

    @Schema(description = "Total somado dos subtotais de todos os pedidos", example = "185.50")
    private double total;

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
