package br.com.drs.radiotv_app_pro.controller;

import br.com.drs.radiotv_app_pro.dto.RamoAtividadeDTO;
import br.com.drs.radiotv_app_pro.service.RamoAtividadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ramoAtividade")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RamoAtividadeController {

    private final RamoAtividadeService ramoAtividadeService;

    @GetMapping
    public ResponseEntity<List<RamoAtividadeDTO>> listarTodos() {
        return ResponseEntity.ok(ramoAtividadeService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<RamoAtividadeDTO> criar(@RequestBody RamoAtividadeDTO dto) {
        RamoAtividadeDTO salvo = ramoAtividadeService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RamoAtividadeDTO> atualizar(@PathVariable Long id, @RequestBody RamoAtividadeDTO dto) {
        RamoAtividadeDTO atualizado = ramoAtividadeService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        ramoAtividadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}