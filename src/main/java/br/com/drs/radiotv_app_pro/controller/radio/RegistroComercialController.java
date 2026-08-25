package br.com.drs.radiotv_app_pro.controller.radio;

import br.com.drs.radiotv_app_pro.dto.radio.RegistroComercialDTO;
import br.com.drs.radiotv_app_pro.service.radio.RegistroComercialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/registroComercial")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RegistroComercialController {

    private final RegistroComercialService service;

    @PostMapping
    public ResponseEntity<RegistroComercialDTO> salvar(@RequestBody RegistroComercialDTO dto) {
        RegistroComercialDTO criado = service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroComercialDTO> buscarPorId(@PathVariable String id) {
        RegistroComercialDTO dto = service.buscarPorId(String.valueOf(id));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroComercialDTO> atualizar(
            @PathVariable String id,
            @RequestBody RegistroComercialDTO dto) {
        RegistroComercialDTO atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<RegistroComercialDTO> inativar(
            @PathVariable String id,
            @RequestBody RegistroComercialDTO dto) {
        RegistroComercialDTO inativado = service.inativar(id, dto);
        return ResponseEntity.ok(inativado);
    }
}