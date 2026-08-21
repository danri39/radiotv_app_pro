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
    public ResponseEntity<MusicasDTO> atualizar(@PathVariable("id") Long id, @RequestBody MusicasDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable("id") Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}