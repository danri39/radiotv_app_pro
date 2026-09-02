package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ProgramaDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.ProgramaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/programa")
@RequiredArgsConstructor
public class ProgramaController {

    private final ProgramaService service;

    @PostMapping
    public ResponseEntity<ProgramaDTO> criar(@RequestBody ProgramaDTO dto) {
        return new ResponseEntity<>(service.salvar(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProgramaDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgramaDTO> atualizar(@PathVariable Long id, @RequestBody ProgramaDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}