package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.PontoDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.PontoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pontos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PontoController {

    private final PontoService pontoService;

    @PostMapping("/registrar-simples/{chaveUsuario}")
    public ResponseEntity<PontoDTO> registrarSimples(
            @PathVariable String chaveUsuario,
            @RequestBody Map<String, String> payload) {
        String tipo = payload.getOrDefault("tipo", "ENTRADA");
        return ResponseEntity.status(HttpStatus.CREATED).body(pontoService.registrarPontoSimples(chaveUsuario, tipo));
    }

    @GetMapping("/inconsistencias")
    public ResponseEntity<List<PontoDTO>> buscarInconsistencias() {
        return ResponseEntity.ok(pontoService.buscarInconsistenciasParaEscritorio());
    }

    @GetMapping("/meus-acertos/{chaveUsuario}")
    public ResponseEntity<List<PontoDTO>> buscarMeusAcertos(@PathVariable String chaveUsuario) {
        return ResponseEntity.ok(pontoService.buscarMeusAcertosPendentes(chaveUsuario));
    }

    @PutMapping("/{id}/solicitar-correcao")
    public ResponseEntity<PontoDTO> solicitarCorrecao(
            @PathVariable Long id,
            @RequestParam String observacao) {
        return ResponseEntity.ok(pontoService.solicitarCorrecaoRH(id, observacao));
    }

    @PutMapping("/corrigir/{id}")
    public ResponseEntity<PontoDTO> corrigirPonto(
            @PathVariable Long id,
            @RequestBody PontoDTO dto) {
        return ResponseEntity.ok(pontoService.realizarCorrecaoFuncionario(id, dto));
    }

    @PostMapping
    public ResponseEntity<PontoDTO> criar(@RequestBody PontoDTO pontoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pontoService.salvar(pontoDTO));
    }

    @GetMapping
    public ResponseEntity<List<PontoDTO>> listarTodos() {
        return ResponseEntity.ok(pontoService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoDTO> buscarPorId(@PathVariable Long id) {
        return pontoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoDTO> atualizar(@PathVariable Long id, @RequestBody PontoDTO pontoDTO) {
        return ResponseEntity.ok(pontoService.atualizar(id, pontoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pontoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}