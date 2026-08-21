package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.GerarRoteiroRequest;
import br.com.drs.radiotv_app_pro.service.escritorio.GeradorRoteiroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/roteiro")
@RequiredArgsConstructor
public class GeradorRoteiroController {

    private final GeradorRoteiroService geradorService;

    // POST /api/programacao/roteiro/gerar
    // Corpo JSON enviado pelo front-end: { "data": "2026-06-12", "diaSemana": "SEXTA" }
    @PostMapping("/gerar")
    public ResponseEntity<String> gerarRoteiro(@RequestBody GerarRoteiroRequest request) {
        try {
            geradorService.gerarRoteiroDoDia(request);
            return ResponseEntity.ok("Roteiro comercial processado e exportado com sucesso.");
        } catch (IllegalArgumentException e) {
            // Retorna o erro amigável de validação de data se o usuário errar o dia da semana
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro interno ao processar o algoritmo de distribuição: " + e.getMessage());
        }
    }
}