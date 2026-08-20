package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FuncionarioBeneficioDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.FuncionarioBeneficioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/funcionarioBeneficio")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FuncionarioBeneficioController {

    private final FuncionarioBeneficioService service;

    @PostMapping
    public ResponseEntity<FuncionarioBeneficioDTO> salvar(@RequestBody FuncionarioBeneficioDTO dto) {
        return new ResponseEntity<>(service.salvar(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioBeneficioDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<FuncionarioBeneficioDTO>> listarPorFuncionario(@PathVariable Long funcionarioId) {
        return ResponseEntity.ok(service.listarPorFuncionario(funcionarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}