package br.com.drs.radiotv_app_pro.controller;

import br.com.drs.radiotv_app_pro.dto.FamiliaDTO;
import br.com.drs.radiotv_app_pro.service.FamiliaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/familia")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FamiliaController {

    private final FamiliaService familiaService;

    @GetMapping
    public ResponseEntity<List<FamiliaDTO>> listarTodos() {
        return ResponseEntity.ok(familiaService.listarTodos());
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<FamiliaDTO>> listarPorFuncionario(@PathVariable Long funcionarioId) {
        return ResponseEntity.ok(familiaService.listarPorFuncionario(funcionarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamiliaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(familiaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FamiliaDTO> criar(@RequestBody FamiliaDTO dto) {
        FamiliaDTO salvo = familiaService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FamiliaDTO> atualizar(@PathVariable Long id, @RequestBody FamiliaDTO dto) {
        FamiliaDTO atualizado = familiaService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        familiaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}