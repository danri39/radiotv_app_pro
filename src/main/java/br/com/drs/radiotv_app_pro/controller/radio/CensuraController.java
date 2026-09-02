package br.com.drs.radiotv_app_pro.controller.radio;

import br.com.drs.radiotv_app_pro.service.radio.CensuraService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/v1/censura")
@RequiredArgsConstructor
public class CensuraController {

    private final CensuraService censuraService;

    @Value("${app.censura.path:C:/RadioTV/Censura}")
    private String censuraPath;

    @PostMapping("/iniciar")
    public ResponseEntity<String> iniciar() {
        censuraService.startRecording();
        return ResponseEntity.ok("Gravação de Censura iniciada.");
    }

    @GetMapping("/arquivos")
    public ResponseEntity<List<String>> listarArquivos() {
        return ResponseEntity.ok(censuraService.listarArquivos());
    }

    @GetMapping("/play/{filename}")
    public ResponseEntity<Resource> streamAudio(@PathVariable String filename) {
        File file = new File(censuraPath, filename);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}