package br.com.drs.radiotv_app_pro.model.radio;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicSearchResult {

    private String id;

    private String title;

    private String artist;

    private Integer duration; // em segundos

    private String url;

    private String thumbnail;

    private String platform;
}