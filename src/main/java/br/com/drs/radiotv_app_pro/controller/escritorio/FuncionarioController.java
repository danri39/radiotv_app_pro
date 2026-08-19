package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FuncionarioDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.service.escritorio.FuncionarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/funcionario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FuncionarioController {

    private final FuncionarioService service;

    @GetMapping
    public ResponseEntity<List<Funcionario>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{chaveUsuario}")
    public ResponseEntity<Funcionario> buscarPorChave(@PathVariable String chaveUsuario) {
        return ResponseEntity.ok(service.buscarPorChaveUsuario(chaveUsuario));
    }

    @PostMapping
    public ResponseEntity<Funcionario> criar(@RequestBody Funcionario funcionario) {
        Funcionario salvo = service.salvar(funcionario);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{chaveUsuario}")
    public ResponseEntity<FuncionarioDTO> atualizar(@PathVariable String chaveUsuario, @RequestBody FuncionarioDTO dto) {
        FuncionarioDTO atualizado = service.atualizar(chaveUsuario, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{chaveUsuario}")
    public ResponseEntity<Void> deletar(@PathVariable String chaveUsuario) {
        service.inativar(chaveUsuario);
        return ResponseEntity.noContent().build();
    }
}