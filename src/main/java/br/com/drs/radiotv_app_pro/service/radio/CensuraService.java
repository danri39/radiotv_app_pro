package br.com.drs.radiotv_app_pro.service.radio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CensuraService {

    @Value("${app.components.path:C:/RadioTV/Componentes}")
    private String componentsPath;

    @Value("${app.censura.path:C:/RadioTV/Censura}")
    private String censuraPath;

    // Nome da entrada de áudio do Windows (dispositivo dshow)
    @Value("${app.censura.audio-device:audio=Linha de Entrada}")
    private String audioDeviceName;

    private Process ffmpegProcess;
    private boolean isRecording = false;

    @PostConstruct
    public void init() {
        File folder = new File(censuraPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Inicia o processo de gravação ininterrupta via FFmpeg
     */
    public synchronized void startRecording() {
        if (isRecording) {
            log.info("A gravação da Censura já está em execução.");
            return;
        }

        Thread recordingThread = new Thread(() -> {
            isRecording = true;
            log.info("Iniciando serviço de gravação da Censura da Rádio...");

            while (isRecording) {
                try {
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
                    File outputFile = new File(censuraPath, "CENSURA_" + timestamp + ".mp3");

                    ProcessBuilder pb = new ProcessBuilder(
                            componentsPath + "/ffmpeg.exe",
                            "-f", "dshow",
                            "-i", audioDeviceName,
                            "-t", "600",                   // Duração exata: 600 segundos (10 minutos)
                            "-ac", "1",                    // Canal: Mono (economiza 50% de espaço)
                            "-ar", "22050",                // Taxa de amostragem: 22.05 kHz (suficiente para voz)
                            "-b:a", "32k",                 // Bitrate ultra-baixo: 32 kbps
                            outputFile.getAbsolutePath()
                    );

                    pb.directory(new File(componentsPath));
                    ffmpegProcess = pb.start();
                    log.info("Gravando bloco de censura: {}", outputFile.getName());

                    // Aguarda os 10 minutos do bloco atual terminarem
                    ffmpegProcess.waitFor();

                } catch (Exception e) {
                    log.error("Erro no ciclo de gravação da Censura: {}", e.getMessage());
                    try {
                        Thread.sleep(5000); // Aguarda 5 segundos antes de tentar reiniciar em caso de erro
                    } catch (InterruptedException ignored) {}
                }
            }
        });

        recordingThread.setDaemon(true);
        recordingThread.start();
    }

    /**
     * Encerra a gravação de forma segura ao desligar a aplicação
     */
    @PreDestroy
    public synchronized void stopRecording() {
        isRecording = false;
        if (ffmpegProcess != null && ffmpegProcess.isAlive()) {
            ffmpegProcess.destroy();
            log.info("Gravação da Censura finalizada.");
        }
    }

    /**
     * Lista todos os arquivos de censura gravados
     */
    public List<String> listarArquivos() {
        File folder = new File(censuraPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp3"));
        List<String> fileList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                fileList.add(file.getName());
            }
        }
        return fileList;
    }

    /**
     * Limpeza automática: Roda todo dia à meia-noite e apaga arquivos com mais de 90 dias
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void limparGravacoesAntigas() {
        log.info("Iniciando rotina de limpeza de gravações de censura (> 90 dias)...");
        File folder = new File(censuraPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp3"));

        if (files != null) {
            Instant limite90Dias = Instant.now().minus(90, ChronoUnit.DAYS);
            int apagados = 0;

            for (File file : files) {
                try {
                    BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    if (attr.creationTime().toInstant().isBefore(limite90Dias)) {
                        if (file.delete()) {
                            apagados++;
                        }
                    }
                } catch (IOException e) {
                    log.error("Falha ao verificar/apagar arquivo antigo: {}", file.getName());
                }
            }
            log.info("Limpeza concluída. {} arquivo(s) de censura antigo(s) removido(s).", apagados);
        }
    }
}