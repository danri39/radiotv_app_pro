package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.GerencialDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.GerencialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/gerencial")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GerencialController {

    private final GerencialService service;

    @GetMapping("/comercial")
    public ResponseEntity<GerencialDTO> obterPainelComercial(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano
    ) {
        LocalDate hoje = LocalDate.now();
        int mesConsulta = (mes != null) ? mes : hoje.getMonthValue();
        int anoConsulta = (ano != null) ? ano : hoje.getYear();

        return ResponseEntity.ok(service.obterPainelComercial(mesConsulta, anoConsulta));
    }
}