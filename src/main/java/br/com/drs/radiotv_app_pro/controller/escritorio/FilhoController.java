package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FilhoDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.FilhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/filho")
@RequiredArgsConstructor
public class FilhoController {

    private final FilhoService service;

    @PostMapping
    public ResponseEntity<FilhoDTO> criar(@RequestBody FilhoDTO dto) {
        FilhoDTO salvo = service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public List<FilhoDTO> listar() {
        return service.listarTodos();
    }

    @GetMapping("/funcionario/{chaveUsuario}")
    public ResponseEntity<List<FilhoDTO>> listarPorFuncionario(@PathVariable String chaveUsuario) {
        return ResponseEntity.ok(service.listarPorFuncionario(chaveUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilhoDTO> atualizar(@PathVariable Long id, @RequestBody FilhoDTO dto) {
        FilhoDTO atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
