package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.AdministracaoDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.AdministracaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/administracao")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdministracaoController {

    private final AdministracaoService service;

    @GetMapping("/resumo")
    public ResponseEntity<AdministracaoDTO> obterResumoGeral() {
        return ResponseEntity.ok(service.calcularResumoGeral());
    }
}