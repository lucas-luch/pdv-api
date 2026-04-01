package com.store.pdvapi.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(name = "ErroResponse", description = "Payload retornado quando uma exceção é lançada.")
public class ErroResponse {

    @Schema(description = "Momento em que o erro ocorreu", example = "2026-04-01T20:12:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código HTTP retornado", example = "404")
    private int status;

    @Schema(description = "Motivo técnico do erro", example = "Recurso não encontrado")
    private String error;

    @Schema(description = "Mensagem amigável explicando o problema", example = "Mesa não encontrada com id: 1")
    private String message;

    @Schema(description = "Caminho da requisição que disparou o erro", example = "/mesas/1")
    private String path;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
