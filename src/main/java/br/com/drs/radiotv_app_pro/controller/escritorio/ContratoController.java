package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ContratoDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.ContratoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contrato")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite que o seu React (localhost:3000 ou 5173) acesse a API sem problemas de CORS
public class ContratoController {

    private final ContratoService service;

    @PostMapping
    public ResponseEntity<ContratoDTO> criar(@RequestBody ContratoDTO dto) {
        return new ResponseEntity<>(service.salvar(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ContratoDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContratoDTO> atualizar(@PathVariable Long id, @RequestBody ContratoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}