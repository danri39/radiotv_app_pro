package br.com.drs.radiotv_app_pro.controller.radio;

import br.com.drs.radiotv_app_pro.dto.radio.PastasDTO;
import br.com.drs.radiotv_app_pro.model.radio.Pastas;
import br.com.drs.radiotv_app_pro.service.radio.PastasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pastas")
@RequiredArgsConstructor
public class PastasController {

    private final PastasService service;

    @PostMapping
    public ResponseEntity<PastasDTO> salvar(@RequestBody PastasDTO dto) {
        PastasDTO pastaSalva = service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pastaSalva);
    }

    @GetMapping
    public ResponseEntity<List<Pastas>> listarTodos() {
        List<Pastas> pastas = service.listarTodos();
        return ResponseEntity.ok(pastas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PastasDTO> atualizar(@PathVariable Long id, @RequestBody PastasDTO dto) {
        PastasDTO pastaAtualizada = service.atualizar(id, dto);
        return ResponseEntity.ok(pastaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
