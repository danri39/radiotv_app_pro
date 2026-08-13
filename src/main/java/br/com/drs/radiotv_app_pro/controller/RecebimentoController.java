package br.com.drs.radiotv_app_pro.controller;

import br.com.drs.radiotv_app_pro.dto.RecebimentoDTO;
import br.com.drs.radiotv_app_pro.model.Recebimento;
import br.com.drs.radiotv_app_pro.service.RecebimentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/recebimento")
@RequiredArgsConstructor
public class RecebimentoController {

    private final RecebimentoService service;

    @PostMapping
    public ResponseEntity<RecebimentoDTO> salvar(RecebimentoDTO dto) {
        RecebimentoDTO salvo = service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Recebimento>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Recebimento>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecebimentoDTO> atualizar(@PathVariable Long id, @RequestBody RecebimentoDTO dto) {
        RecebimentoDTO recebimentoExistente = service.atualizar(id, dto);
        return ResponseEntity.ok(recebimentoExistente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RecebimentoDTO> deletar(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}