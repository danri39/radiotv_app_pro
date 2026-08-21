package br.com.drs.radiotv_app_pro.controller.radio;

import br.com.drs.radiotv_app_pro.model.radio.DownloadTask;
import br.com.drs.radiotv_app_pro.model.radio.MusicSearchResult;
import br.com.drs.radiotv_app_pro.service.radio.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/music")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MusicController {

    private final MusicService musicService;

    @GetMapping("/search")
    public ResponseEntity<List<MusicSearchResult>> searchMusic(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int maxResults) {

        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            if (maxResults < 1 || maxResults > 50) {
                maxResults = 10;
            }

            List<MusicSearchResult> results = musicService.searchMusic(query, maxResults);
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            log.error("Erro na busca: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/download")
    public ResponseEntity<Map<String, Object>> downloadMusic(
            @RequestBody MusicSearchResult musicResult) {

        try {
            if (musicResult == null || musicResult.getUrl() == null) {
                return ResponseEntity.badRequest().body(null);
            }

            DownloadTask task = musicService.downloadMusic(musicResult);

            Map<String, Object> response = new HashMap<>();
            response.put("taskId", task.getId());
            response.put("status", task.getStatus());
            response.put("message", "Download iniciado com sucesso");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erro ao iniciar download: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/download/batch")
    public ResponseEntity<Map<String, Object>> downloadMultipleMusic(
            @RequestBody List<MusicSearchResult> musicResults) {

        try {
            if (musicResults == null || musicResults.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            List<Map<String, String>> tasks = new java.util.ArrayList<>();

            for (MusicSearchResult music : musicResults) {
                DownloadTask task = musicService.downloadMusic(music);

                Map<String, String> taskInfo = new HashMap<>();
                taskInfo.put("taskId", task.getId());
                taskInfo.put("title", music.getTitle());
                taskInfo.put("artist", music.getArtist());

                tasks.add(taskInfo);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("tasks", tasks);
            response.put("total", tasks.size());
            response.put("message", "Downloads iniciados com sucesso");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erro ao iniciar downloads em lote: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/download/status/{taskId}")
    public ResponseEntity<DownloadTask> getDownloadStatus(@PathVariable String taskId) {
        try {
            DownloadTask task = musicService.getDownloadStatus(taskId);

            if (task == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(task);

        } catch (Exception e) {
            log.error("Erro ao verificar status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
}