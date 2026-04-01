package com.store.pdvapi.exception;

import com.store.pdvapi.dto.error.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MesaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handleMesaNaoEncontrada(MesaNaoEncontradaException ex,
            HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({MesaStatusInvalidoException.class, MesaNumeroDuplicadoException.class,
            MesaNumeroObrigatorioException.class})
    public ResponseEntity<ErroResponse> handleMesaInvalidas(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({PedidoNaoEncontradoException.class})
    public ResponseEntity<ErroResponse> handlePedidoNaoEncontrado(PedidoNaoEncontradoException ex,
            HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({PedidoStatusInvalidoException.class})
    public ResponseEntity<ErroResponse> handlePedidoStatusInvalidos(PedidoStatusInvalidoException ex,
            HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ProdutoNaoEncontradoException.class, ProdutoInativoException.class})
    public ResponseEntity<ErroResponse> handleProdutoInvalidos(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(BancoDeDadosException.class)
    public ResponseEntity<ErroResponse> handleBancoDeDados(BancoDeDadosException ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenerico(Exception ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErroResponse> buildResponse(Exception ex, HttpStatus status,
            HttpServletRequest request) {
        ErroResponse response = new ErroResponse();
        response.setTimestamp(java.time.LocalDateTime.now());
        response.setStatus(status.value());
        response.setError(status.getReasonPhrase());
        response.setMessage(ex.getMessage());
        response.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(response);
    }
}
