package br.com.drs.radiotv_app_pro.controller.radio;

import br.com.drs.radiotv_app_pro.dto.radio.MusicasDTO;
import br.com.drs.radiotv_app_pro.service.radio.MusicasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/musicas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MusicasController {

    private final MusicasService musicaService;

    @GetMapping
    public ResponseEntity<List<MusicasDTO>> listarTodas() {
        return ResponseEntity.ok(musicaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MusicasDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(musicaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MusicasDTO> criar(@RequestBody MusicasDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(musicaService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MusicasDTO> atualizar(@PathVariable Long id,
                                                @RequestBody MusicasDTO dto) {
        return ResponseEntity.ok(musicaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        musicaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}