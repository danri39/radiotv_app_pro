package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ContratoMidiaDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.ContratoMidiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contratoMidia")
@RequiredArgsConstructor
public class ContratoMidiaController {

    private final ContratoMidiaService service;

    @PostMapping
    public ResponseEntity<ContratoMidiaDTO> criar(@RequestBody ContratoMidiaDTO dto) {
        return new ResponseEntity<>(service.salvar(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ContratoMidiaDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoMidiaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContratoMidiaDTO> atualizar(@PathVariable Long id, @RequestBody ContratoMidiaDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}