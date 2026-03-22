package com.store.pdvapi.controller;

import com.store.pdvapi.dto.mesa.AtualizarStatusMesaRequest;
import com.store.pdvapi.dto.mesa.CriarMesaRequest;
import com.store.pdvapi.dto.mesa.MesaResponse;
import com.store.pdvapi.service.MesaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mesas")
public class MesaController {

    private final MesaService service;

    public MesaController(MesaService service) {
        this.service = service;
    }

    @PostMapping
    public MesaResponse criar(@RequestBody CriarMesaRequest request) {
        return service.criar(request);
    }

    @GetMapping
    public List<MesaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public MesaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PatchMapping("/{id}/abrir")
    public MesaResponse abrir(@PathVariable Long id, @RequestBody AtualizarStatusMesaRequest request) {
        return service.abrir(id, request);
    }

    @PatchMapping("/{id}/fechar")
    public MesaResponse fechar(@PathVariable Long id, @RequestBody AtualizarStatusMesaRequest request) {
        return service.fechar(id, request);
    }

    @PatchMapping("/{id}/liberar")
    public MesaResponse liberar(@PathVariable Long id, @RequestBody AtualizarStatusMesaRequest request) {
        return service.liberar(id, request);
    }
}
