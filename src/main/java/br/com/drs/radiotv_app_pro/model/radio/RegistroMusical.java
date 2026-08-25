package br.com.drs.radiotv_app_pro.model.radio;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "registro_musical")
public class RegistroMusical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
