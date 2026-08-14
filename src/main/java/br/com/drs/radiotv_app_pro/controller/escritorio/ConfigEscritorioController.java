package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ConfigEscritorioDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.ConfigEscritorio;
import br.com.drs.radiotv_app_pro.service.escritorio.ConfigEscritorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/configEscritorio")
@RequiredArgsConstructor
public class ConfigEscritorioController {

    private final ConfigEscritorioService service;

    @PostMapping
    public ResponseEntity<ConfigEscritorioDTO> salvar(@RequestBody ConfigEscritorioDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @GetMapping("/{id}")
    public Optional<ConfigEscritorio> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfigEscritorioDTO> atualizar(@PathVariable Long id, @RequestBody ConfigEscritorioDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}