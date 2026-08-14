package br.com.drs.radiotv_app_pro.controller;

import br.com.drs.radiotv_app_pro.model.Ponto;
import br.com.drs.radiotv_app_pro.service.PontoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ponto")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PontoController {

    private final PontoService pontoService;

    @PostMapping("/bater-ponto/{usuarioId}")
    public ResponseEntity<?> baterPonto(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(pontoService.baterPontoSequencial(usuarioId));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro interno ao processar batida de ponto."));
        }
    }

    @GetMapping("/meus-acertos/{usuarioId}")
    public ResponseEntity<List<Ponto>> obterMeusPontosParaCorrigir(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(pontoService.listarMeusPontosParaCorrigir(usuarioId));
    }

    @PutMapping("/corrigir/{id}")
    public ResponseEntity<?> realizarAcertoPeloFuncionario(@PathVariable Long id, @RequestBody Ponto dadosCorrigidos) {
        try {
            return ResponseEntity.ok(pontoService.corrigirPontoPeloFuncionario(id, dadosCorrigidos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/escritorio/sinalizar-erro/{id}")
    public ResponseEntity<Ponto> sinalizarInconsistencia(
            @PathVariable Long id,
            @RequestParam String motivo,
            @RequestParam String recado) {
        return ResponseEntity.ok(pontoService.sinalizarInconsistencia(id, motivo, recado));
    }

    @GetMapping("/escritorio/todos")
    public ResponseEntity<List<Ponto>> listarTodosParaOEscritorio() {
        return ResponseEntity.ok(pontoService.listarPontosComErroParaOEscritorio());
    }
}