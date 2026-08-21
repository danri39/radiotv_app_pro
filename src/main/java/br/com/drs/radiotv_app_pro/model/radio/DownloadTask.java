package br.com.drs.radiotv_app_pro.model.radio;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadTask {

    private String id;

    private MusicSearchResult musicResult;

    private String status; // pending, downloading, completed, failed

    private Double progress; // 0 a 100

    private String filePath;

    private String errorMessage;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
}