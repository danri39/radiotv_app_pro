package br.com.drs.radiotv_app_pro.controller.radio;

import br.com.drs.radiotv_app_pro.dto.radio.MusicasDTO;
import br.com.drs.radiotv_app_pro.service.radio.MusicasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/musicas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MusicasController {

    private final MusicasService service;

    @PostMapping("/escanear")
    public ResponseEntity<?> escanearEGerarTxt() {
        try {
            // Chama a varredura e a gravação padronizada no Service
            service.escanearPastaESincronizarTxt();
            return ResponseEntity.ok().body("Músicas escaneadas e arquivo Musicas.txt gerado com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(STR."Erro ao escanear pasta: \{e.getMessage()}");
        }
    }

    @PostMapping("/restaurar")
    public ResponseEntity<Void> restaurarDoTxt() {
        service.restaurarBancoDoArquivoTxt();
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<MusicasDTO> salvar(@Valid @RequestBody MusicasDTO dto) {
        MusicasDTO musicaSalva = service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(musicaSalva);
    }

    @GetMapping
    public ResponseEntity<List<MusicasDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MusicasDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/musica/{nome}")
    public ResponseEntity<Optional<MusicasDTO>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    @GetMapping("/artista/{artista}")
    public ResponseEntity<Optional<MusicasDTO>> buscarPorArtista(@PathVariable String artista) {
        return ResponseEntity.ok(service.buscarPorArtista(artista));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MusicasDTO> atualizar(@PathVariable Long id, @RequestBody MusicasDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}