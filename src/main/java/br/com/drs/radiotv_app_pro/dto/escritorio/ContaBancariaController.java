package br.com.drs.radiotv_app_pro.dto.escritorio;

import br.com.drs.radiotv_app_pro.service.escritorio.ContaBancariaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contasBancaria")
@RequiredArgsConstructor
public class ContaBancariaController {

    private final ContaBancariaService service;

    @PostMapping
    public ResponseEntity<ContaBancariaDTO> criar(@RequestBody ContaBancariaDTO dto) {
        return new ResponseEntity<>(service.salvar(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ContaBancariaDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaBancariaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaBancariaDTO> atualizar(@PathVariable Long id, @RequestBody ContaBancariaDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}