package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FeriasDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.model.escritorio.Usuario;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.UsuarioRepository;
import br.com.drs.radiotv_app_pro.service.escritorio.FeriasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ferias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FeriasController {

    private final FeriasService service;
    private final UsuarioRepository usuarioRepository;
    private final FuncionarioRepository funcionarioRepository;

    @PostMapping
    public ResponseEntity<FeriasDTO> criar(@RequestBody FeriasDTO dto) {
        return new ResponseEntity<>(service.salvar(dto), HttpStatus.CREATED);
    }

    @PostMapping("/meu-agendamento/{usuarioId}")
    public ResponseEntity<FeriasDTO> agendarPeloUsuario(@PathVariable Long usuarioId, @RequestBody FeriasDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário de acesso não encontrado."));

        if (usuario.getFuncionarioId() == null) {
            throw new IllegalStateException("Seu usuário não possui um funcionário CLT vinculado pelo administrador.");
        }

        Funcionario funcionario = funcionarioRepository.findById(usuario.getFuncionarioId())
                .orElseThrow(() -> new IllegalStateException("Funcionário vinculado ao usuário não encontrado."));

        dto.setId(usuarioId);
        return new ResponseEntity<>(service.salvar(dto), HttpStatus.CREATED);
    }

    @GetMapping("/meu-status/{usuarioId}")
    public ResponseEntity<List<FeriasDTO>> buscarMinhasFerias(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (usuario.getFuncionarioId() == null) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(service.listarPorFuncionario(usuario.getFuncionarioId()));
    }

    @GetMapping
    public ResponseEntity<List<FeriasDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeriasDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeriasDTO> atualizar(@PathVariable Long id, @RequestBody FeriasDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}