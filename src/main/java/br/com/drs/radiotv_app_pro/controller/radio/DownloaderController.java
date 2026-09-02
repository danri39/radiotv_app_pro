package br.com.drs.radiotv_app_pro.controller.radio;

import br.com.drs.radiotv_app_pro.service.radio.DownloaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/downloader")
@RequiredArgsConstructor
public class DownloaderController {

    private final DownloaderService service;

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, String>>> search(@RequestParam String q) {
        try {
            return ResponseEntity.ok(service.search(q));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    @PostMapping("/download")
    public ResponseEntity<String> download(@RequestBody Map<String, String> payload) {
        try {
            String file = service.downloadAndConvert(
                    payload.get("id"),
                    payload.get("title"),
                    payload.get("artist")
            );
            return ResponseEntity.ok("Baixado com sucesso: " + file);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }
}