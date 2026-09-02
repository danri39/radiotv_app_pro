package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.LoginResponseDTO;
import br.com.drs.radiotv_app_pro.dto.escritorio.UsuarioDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Usuario;
import br.com.drs.radiotv_app_pro.service.escritorio.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/usuario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> buscarTodos() {
        return ResponseEntity.ok(usuarioService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<Optional<Usuario.UsuarioResumoProjection>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(usuarioService.buscarPorNome(nome));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/public/cadastrar")
    public ResponseEntity<String> cadastrar(@RequestBody UsuarioDTO dto) {
        usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Cadastro realizado! Verifique seu e-mail para obter suas chaves de acesso.");
    }

    @PostMapping("/public/primeiro-acesso")
    public ResponseEntity<String> primeiroAcesso(@RequestBody UsuarioDTO dto) {
        usuarioService.definirSenhaPrimeiroAcesso(dto);
        return ResponseEntity.ok("Senha criada com sucesso! Redirecionando para o login.");
    }

    @PostMapping("/public/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.login(dto));
    }

    @PostMapping("/public/esqueci-senha")
    public ResponseEntity<String> esqueciSenha(@RequestBody UsuarioDTO dto) {
        usuarioService.solicitarTrocaSenha(dto);
        return ResponseEntity.ok("Se a chave for válida, um e-mail com as instruções foi enviado.");
    }

    @PostMapping("/public/trocar-senha")
    public ResponseEntity<String> trocarSenha(@RequestBody UsuarioDTO dto) {
        usuarioService.efetivarTrocaSenha(dto);
        return ResponseEntity.ok("Senha alterada com sucesso! Redirecionando para o login.");
    }
}