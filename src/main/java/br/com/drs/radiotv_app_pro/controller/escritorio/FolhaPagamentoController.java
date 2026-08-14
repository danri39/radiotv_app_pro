package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FolhaPagamentoDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.FolhaPagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/folhasPagamento")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FolhaPagamentoController {

    private final FolhaPagamentoService service;

    @PostMapping
    public ResponseEntity<FolhaPagamentoDTO> gerarFolha(@RequestBody FolhaPagamentoDTO dto) {
        return new ResponseEntity<>(service.calcularFolha(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FolhaPagamentoDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<FolhaPagamentoDTO>> listarPorFuncionario(@PathVariable Long funcionarioId) {
        return ResponseEntity.ok(service.listarPorFuncionario(funcionarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}