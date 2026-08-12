package br.com.drs.radiotv_app_pro.controller;

import br.com.drs.radiotv_app_pro.dto.AgenciaDTO;
import br.com.drs.radiotv_app_pro.service.AgenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agencia")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AgenciaController {

    private final AgenciaService agenciaService;

    @GetMapping
    public ResponseEntity<List<AgenciaDTO>> listarTodas() {
        return ResponseEntity.ok(agenciaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgenciaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(agenciaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AgenciaDTO> criar(@RequestBody AgenciaDTO dto) {
        AgenciaDTO salvo = agenciaService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgenciaDTO> atualizar(@PathVariable Long id, @RequestBody AgenciaDTO dto) {
        AgenciaDTO atualizado = agenciaService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        agenciaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}