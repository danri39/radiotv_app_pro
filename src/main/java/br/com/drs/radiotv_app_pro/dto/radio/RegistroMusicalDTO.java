package br.com.drs.radiotv_app_pro.dto.radio;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroMusicalDTO {

    private String id;

    private String Artista;

    private String Musica;

    @JsonFormat(pattern = "mm:ss")
    private LocalTime introducao;

    @JsonFormat(pattern = "mm:ss")
    private LocalTime tempo;

    private String observacao;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate inicio;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate termino;

    private String pastaArquivo;

    @Builder.Default
    private Boolean ativo = true;
}
