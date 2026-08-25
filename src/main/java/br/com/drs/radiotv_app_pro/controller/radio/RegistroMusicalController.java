package br.com.drs.radiotv_app_pro.controller.radio;

import br.com.drs.radiotv_app_pro.dto.radio.RegistroMusicalDTO;
import br.com.drs.radiotv_app_pro.model.radio.RegistroMusical;
import br.com.drs.radiotv_app_pro.service.radio.RegistroMusicalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/registroMusical")
@RequiredArgsConstructor
public class RegistroMusicalController {
    
    private final RegistroMusicalService service;

    @PostMapping
    public ResponseEntity<RegistroMusicalDTO> salvar(@RequestBody RegistroMusicalDTO dto) {
        RegistroMusicalDTO criado = service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<RegistroMusical>> buscarPorId(@PathVariable String id) {
        Optional<RegistroMusical> dto = service.buscarPorId(String.valueOf(id));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroMusical> atualizar(
            @PathVariable String id,
            @RequestBody RegistroMusicalDTO dto) {
        RegistroMusical atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<RegistroMusicalDTO> inativar(
            @PathVariable String id,
            @RequestBody RegistroMusicalDTO dto) {
        RegistroMusicalDTO inativado = service.inativar(id, dto);
        return ResponseEntity.ok(inativado);
    }
}