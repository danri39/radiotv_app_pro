package br.com.drs.radiotv_app_pro.service.radio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DownloaderService {

    @Value("${app.components.path:C:/RadioTV/Componentes}")
    private String componentsPath;

    @Value("${app.musicas.path:C:/RadioTV/Musicas}")
    private String musicasPath;

    /**
     * Busca no YouTube usando yt-dlp
     */
    public List<Map<String, String>> search(String query) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                componentsPath + "/yt-dlp.exe",
                "--flat-playlist",
                "--print", "%(title)s|%(id)s|%(duration_string)s|%(uploader)s",
                "ytsearch10:" + query
        );

        pb.directory(new File(componentsPath));
        Process process = pb.start();

        List<Map<String, String>> results = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    Map<String, String> item = new HashMap<>();
                    item.put("title", parts[0]);
                    item.put("id", parts[1]);
                    item.put("duration", parts[2]);
                    item.put("artist", parts.length > 3 ? parts[3] : "Desconhecido");
                    results.add(item);
                }
            }
        }
        return results;
    }

    /**
     * Baixa apenas o áudio, converte para MP3 via FFmpeg e salva no diretório de músicas
     */
    /**
     * Baixa apenas o áudio, converte para MP3 via FFmpeg e salva no diretório de músicas
     */
    /**
     * Baixa apenas o áudio, converte para MP3 via FFmpeg e salva no diretório de músicas
     */
    public String downloadAndConvert(String videoId, String title, String artist) throws IOException, InterruptedException {
        // Sanitiza caracteres especiais para evitar falhas no nome do arquivo no Windows
        String safeArtist = artist.replaceAll("[^a-zA-Z0-9\\s_-]", "").trim();
        String safeTitle = title.replaceAll("[^a-zA-Z0-9\\s_-]", "").trim();

        // Garante que a pasta Musicas exista no disco
        File outputFolder = new File(musicasPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // Caminho absoluto completo para o arquivo final (sem a extensão, o yt-dlp colocará .mp3)
        File outputFile = new File(outputFolder, safeArtist + " - " + safeTitle + ".%(ext)s");

        ProcessBuilder pb = new ProcessBuilder(
                componentsPath + "/yt-dlp.exe",
                "-x",                             // Extrai o áudio
                "--audio-format", "mp3",          // Converte para MP3
                "--audio-quality", "0",            // Qualidade máxima VBR
                "--ffmpeg-location", componentsPath, // Pasta onde estão ffmpeg.exe e ffprobe.exe
                "-o", outputFile.getAbsolutePath(),  // Caminho absoluto completo
                "https://www.youtube.com/watch?v=" + videoId
        );

        pb.directory(new File(componentsPath));
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Captura e exibe o log exato de onde o yt-dlp está salvando o arquivo
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("[yt-dlp] " + line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            String finalFileName = safeArtist + " - " + safeTitle + ".mp3";
            log.info("Download concluído e salvo em: {}/{}", musicasPath, finalFileName);
            return finalFileName;
        } else {
            log.error("Falha ao baixar áudio ID: {}. Exit code: {}", videoId, exitCode);
            throw new RuntimeException("Erro ao baixar/converter o áudio do vídeo ID: " + videoId);
        }
    }
}