package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ContratoPagamentoDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.ContratoPagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contratoPagamento")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Liberado para o seu React consumir sem bloqueios de CORS
public class ContratoPagamentoController {

    private final ContratoPagamentoService service;

    @PostMapping
    public ResponseEntity<ContratoPagamentoDTO> criar(@RequestBody ContratoPagamentoDTO dto) {
        return new ResponseEntity<>(service.salvar(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ContratoPagamentoDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoPagamentoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContratoPagamentoDTO> atualizar(@PathVariable Long id, @RequestBody ContratoPagamentoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // AÇÃO EXCLUSIVA: Executa o faturamento da parcela
    @PutMapping("/{id}/faturar")
    public ResponseEntity<ContratoPagamentoDTO> faturar(@PathVariable Long id) {
        return ResponseEntity.ok(service.faturarParcela(id));
    }

    // AÇÃO EXCLUSIVA: Executa a baixa de pagamento da parcela
    @PutMapping("/{id}/baixar")
    public ResponseEntity<ContratoPagamentoDTO> baixar(@PathVariable Long id) {
        return ResponseEntity.ok(service.baixarParcela(id));
    }
}