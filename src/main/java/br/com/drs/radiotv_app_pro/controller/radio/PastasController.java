package br.com.drs.radiotv_app_pro.controller.radio;

import br.com.drs.radiotv_app_pro.dto.radio.PastasDTO;
import br.com.drs.radiotv_app_pro.model.radio.Pastas;
import br.com.drs.radiotv_app_pro.service.radio.PastasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pastas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PastasController {

    private final PastasService service;

    @PostMapping
    public ResponseEntity<PastasDTO> salve(@RequestBody PastasDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping
    public List<Pastas> listAll() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Optional<Pastas> findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PastasDTO> update(@PathVariable Long id, @RequestBody PastasDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.delete(id);
    }
}