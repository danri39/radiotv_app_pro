package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FeriasDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.FeriasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ferias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FeriasController {

    private final FeriasService service;

    @PostMapping
    public ResponseEntity<FeriasDTO> criar(@RequestBody FeriasDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<FeriasDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/usuario/{chaveUsuario}")
    public ResponseEntity<List<FeriasDTO>> listarPorUsuario(@PathVariable String chaveUsuario) {
        return ResponseEntity.ok(service.listarPorChaveUsuario(chaveUsuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeriasDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeriasDTO> atualizar(@PathVariable Long id, @RequestBody FeriasDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}