package br.com.drs.radiotv_app_pro.service.radio;

import br.com.drs.radiotv_app_pro.model.radio.DownloadTask;
import br.com.drs.radiotv_app_pro.model.radio.MusicSearchResult;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class MusicService {

    // Caminhos absolutos para os executáveis em C:\RadioTV\Ferramentas
    private static final String YT_DLP_PATH = "C:\\RadioTV\\Ferramentas\\yt-dlp.exe";
    private static final String FFMPEG_DIR = "C:\\RadioTV\\Ferramentas";

    @Value("${app.download.folder:C:\\RadioTV\\Musicas}")
    private String downloadFolder;

    private final Map<String, DownloadTask> activeDownloads = new ConcurrentHashMap<>();

    /**
     * Tenta atualizar o yt-dlp automaticamente ao iniciar o serviço Spring
     */
    @PostConstruct
    public void autoUpdateYtDlp() {
        try {
            log.info("Verificando atualizações do yt-dlp...");
            ProcessBuilder pb = new ProcessBuilder(YT_DLP_PATH, "-U");
            pb.redirectErrorStream(true);
            Process p = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("[yt-dlp update] {}", line);
                }
            }
            p.waitFor();
        } catch (Exception e) {
            log.warn("Não foi possível atualizar o yt-dlp automaticamente: {}", e.getMessage());
        }
    }

    public List<MusicSearchResult> searchMusic(String query, int maxResults) {
        try {
            log.info("Buscando músicas: {}", query);

            List<String> command = Arrays.asList(
                    YT_DLP_PATH,
                    "--no-update",
                    "--flat-playlist",
                    "--dump-json",
                    STR."ytsearch\{maxResults}:\{query}"
            );

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException(STR."Erro na busca: código de saída \{exitCode}");
            }

            return parseSearchResults(output.toString());

        } catch (Exception e) {
            log.error("Erro ao buscar músicas: {}", e.getMessage(), e);
            throw new RuntimeException(STR."Erro ao buscar músicas: \{e.getMessage()}");
        }
    }

    public DownloadTask downloadMusic(MusicSearchResult musicResult) {
        String taskId = UUID.randomUUID().toString();

        DownloadTask task = DownloadTask.builder()
                .id(taskId)
                .musicResult(musicResult)
                .status("pending")
                .progress(0.0)
                .createdAt(LocalDateTime.now())
                .build();

        activeDownloads.put(taskId, task);

        Thread downloadThread = new Thread(() -> downloadWorker(taskId, musicResult));
        downloadThread.setDaemon(true);
        downloadThread.start();

        log.info("Download iniciado: {} - {}", musicResult.getArtist(), musicResult.getTitle());
        return task;
    }

    public DownloadTask getDownloadStatus(String taskId) {
        return activeDownloads.get(taskId);
    }

    private void downloadWorker(String taskId, MusicSearchResult musicResult) {
        DownloadTask task = activeDownloads.get(taskId);

        try {
            task.setStatus("downloading");

            String filename = generateFilename(musicResult.getArtist(), musicResult.getTitle());
            String outputPath = Paths.get(downloadFolder, filename).toString();

            Files.createDirectories(Paths.get(downloadFolder));

            Process process = getProcess(musicResult, outputPath);
            StringBuilder processOutput = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processOutput.append(line).append("\n");
                    log.info("[yt-dlp log] {}", line);

                    Pattern pattern = Pattern.compile("(\\d+\\.?\\d*)%");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        try {
                            double progress = Double.parseDouble(matcher.group(1));
                            task.setProgress(progress);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                File mp3File = new File(outputPath);
                if (mp3File.exists()) {
                    task.setFilePath(outputPath);
                    task.setStatus("completed");
                    task.setProgress(100.0);
                    task.setCompletedAt(LocalDateTime.now());
                    log.info("Download concluído: {}", filename);
                } else {
                    throw new RuntimeException("Arquivo não encontrado após download");
                }
            } else {
                log.error("Erro no yt-dlp (Código {}): \n{}", exitCode, processOutput.toString());
                throw new RuntimeException(STR."Erro no download: código de saída \{exitCode}. Detalhes: \{processOutput.toString()}");
            }

        } catch (Exception e) {
            task.setStatus("failed");
            task.setErrorMessage(e.getMessage());
            log.error("Erro no download: {}", e.getMessage(), e);
        }
    }

    private static @NonNull Process getProcess(MusicSearchResult musicResult, String outputPath) throws IOException {
        String videoUrl = musicResult.getUrl();

        if (videoUrl != null && !videoUrl.startsWith("http")) {
            videoUrl = STR."https://www.youtube.com/watch?v=\{videoUrl}";
        }

        List<String> command = Arrays.asList(
                YT_DLP_PATH,
                "--no-update",
                "-x",
                "--audio-format", "mp3",
                "--audio-quality", "192K",
                "--ffmpeg-location", FFMPEG_DIR,
                "--no-playlist",
                "-o", outputPath.replace(".mp3", ".%(ext)s"),
                videoUrl
        );

        log.info("Executando comando yt-dlp: {}", String.join(" ", command));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        return processBuilder.start();
    }

    private List<MusicSearchResult> parseSearchResults(String jsonOutput) {
        List<MusicSearchResult> results = new ArrayList<>();
        String[] lines = jsonOutput.split("\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            try {
                String id = extractJsonField(line, "id");
                String rawTitle = extractJsonField(line, "title");
                String url = extractJsonField(line, "url");
                String webpageUrl = extractJsonField(line, "webpage_url");
                String durationStr = extractJsonField(line, "duration");
                String thumbnail = extractJsonField(line, "thumbnail");

                if (rawTitle != null && !rawTitle.isEmpty()) {
                    Integer duration = null;
                    if (durationStr != null && !durationStr.equals("null")) {
                        try {
                            duration = (int) Float.parseFloat(durationStr);
                        } catch (NumberFormatException ignored) {}
                    }

                    // Limpa o título antes de processar artista/música
                    String cleanRawTitle = cleanTitle(rawTitle);

                    // Extrai e formata o Artista e a Música sem duplicidades
                    String[] parsed = extractArtistAndTitle(cleanRawTitle);
                    String artist = parsed[0];
                    String title = parsed[1];

                    String finalUrl;
                    if (webpageUrl != null && !webpageUrl.equals("null") && webpageUrl.startsWith("http")) {
                        finalUrl = webpageUrl;
                    } else if (url != null && !url.equals("null") && url.startsWith("http")) {
                        finalUrl = url;
                    } else if (id != null && !id.equals("null")) {
                        finalUrl = STR."https://www.youtube.com/watch?v=\{id}";
                    } else {
                        finalUrl = url;
                    }

                    MusicSearchResult result = MusicSearchResult.builder()
                            .id(id != null ? id : UUID.randomUUID().toString())
                            .title(title)
                            .artist(artist)
                            .duration(duration)
                            .url(finalUrl)
                            .thumbnail(thumbnail)
                            .platform("youtube")
                            .build();

                    results.add(result);
                }
            } catch (Exception e) {
                log.warn("Erro ao parsear resultado: {}", e.getMessage());
            }
        }

        log.info("Busca retornou {} resultados", results.size());
        return results;
    }

    private String extractJsonField(String json, String field) {
        String pattern = "\"" + field + "\":\\s*\"?([^\",}]+)\"?";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(json);

        if (m.find()) {
            String value = m.group(1);
            return "null".equals(value) ? null : value;
        }
        return null;
    }

    /**
     * Separa o artista e o título evitando que o nome do artista seja duplicado
     */
    private String[] extractArtistAndTitle(String title) {
        // Trata separadores padrão "Artista - Titulo"
        Pattern pattern = Pattern.compile("(.+?)\\s*[-–—]\\s*(.+)");
        Matcher matcher = pattern.matcher(title);

        if (matcher.find()) {
            String candidateArtist = matcher.group(1).trim();
            String candidateTitle = matcher.group(2).trim();

            // Se o titulo resultante ainda começar com o artista, remove a duplicação
            if (candidateTitle.toLowerCase().startsWith(candidateArtist.toLowerCase())) {
                candidateTitle = candidateTitle.substring(candidateArtist.length()).replaceAll("^[\\s\\-–—]+", "").trim();
            }

            return new String[]{candidateArtist, candidateTitle};
        }

        return new String[]{"Desconhecido", title.trim()};
    }

    private String cleanTitle(String title) {
        // Remove trechos como (Official Video), [Audio], etc.
        title = title.replaceAll("(?i)\\s*[\\[\\(](official|video|lyric|audio|hd|4k|remastered|clip).*?[\\]\\)]\\s*", " ");

        // Remove prefixos soltos de caractere único ou filtro de busca (ex: "a - ")
        title = title.replaceAll("^[a-zA-Z]\\s*[-–—]\\s*", "");

        return title.replaceAll("\\s+", " ").trim();
    }

    private String generateFilename(String artist, String title) {
        String safeArtist = sanitizeFilename(artist);
        String safeTitle = sanitizeFilename(title);

        String filename;
        if ("Desconhecido".equalsIgnoreCase(safeArtist) || safeTitle.toLowerCase().startsWith(safeArtist.toLowerCase())) {
            filename = STR."\{safeTitle}.mp3";
        } else {
            filename = STR."\{safeArtist} - \{safeTitle}.mp3";
        }

        Path filePath = Paths.get(downloadFolder, filename);
        int counter = 1;
        String baseName = filename.replace(".mp3", "");

        while (Files.exists(filePath)) {
            filename = STR."\{baseName} (\{counter}).mp3";
            filePath = Paths.get(downloadFolder, filename);
            counter++;
        }

        return filename;
    }

    private String sanitizeFilename(String name) {
        String sanitized = name.replaceAll("[<>:\"/\\\\|?*]", "");
        sanitized = sanitized.replaceAll("\\s+", " ").trim();

        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }

        return sanitized;
    }
}