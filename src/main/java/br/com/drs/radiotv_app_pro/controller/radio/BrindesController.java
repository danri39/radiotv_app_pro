package br.com.drs.radiotv_app_pro.controller.radio;

import br.com.drs.radiotv_app_pro.dto.radio.BrindesDTO;
import br.com.drs.radiotv_app_pro.model.radio.Brindes;
import br.com.drs.radiotv_app_pro.service.radio.BrindesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/brindes")
@RequiredArgsConstructor
public class BrindesController {

    private final BrindesService service;

    @PostMapping
    public ResponseEntity<BrindesDTO> addBrindes(@RequestBody BrindesDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping
    public List<Brindes> getAllBrindes() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Brindes> getBrindesById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrindesDTO> updateBrindes(@PathVariable Long id, @RequestBody BrindesDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.delete(id);
    }
}