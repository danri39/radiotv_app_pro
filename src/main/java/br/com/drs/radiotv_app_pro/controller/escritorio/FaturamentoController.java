package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FaturamentoDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.FaturamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/contratoPagamento")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FaturamentoController {

    private final FaturamentoService service;

    @PostMapping
    public ResponseEntity<FaturamentoDTO> criar(@RequestBody FaturamentoDTO dto) {
        return new ResponseEntity<>(service.salvar(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FaturamentoDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaturamentoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FaturamentoDTO> atualizar(@PathVariable Long id, @RequestBody FaturamentoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/faturar")
    public ResponseEntity<FaturamentoDTO> faturar(@PathVariable Long id) {
        return ResponseEntity.ok(service.faturarParcela(id));
    }

    @PutMapping("/{id}/baixar")
    public ResponseEntity<FaturamentoDTO> baixar(@PathVariable Long id) {
        return ResponseEntity.ok(service.baixarParcela(id));
    }

    @PutMapping("/{id}/estornar")
    public ResponseEntity<FaturamentoDTO> estornar(@PathVariable Long id) {
        return ResponseEntity.ok(service.estornarParcela(id));
    }
}