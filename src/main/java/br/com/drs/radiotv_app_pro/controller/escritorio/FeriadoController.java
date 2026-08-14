package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FeriadoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Feriado;
import br.com.drs.radiotv_app_pro.service.escritorio.FeriadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/feriado")
@RequiredArgsConstructor
public class FeriadoController {

    private final FeriadoService service;

    @PostMapping
    public ResponseEntity<FeriadoDTO> salvar(@RequestBody FeriadoDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @GetMapping
    public List<Feriado> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Optional<Feriado> buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeriadoDTO> atualizar(@PathVariable Long id, @RequestBody FeriadoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}