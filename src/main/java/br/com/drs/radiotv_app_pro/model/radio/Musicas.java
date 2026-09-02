package br.com.drs.radiotv_app_pro.model.radio;

import br.com.drs.radiotv_app_pro.model.enuns.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "musicas")
public class Musicas {

    @Id
    private Long id;

    @Column(nullable = false)
    private String artista;

    @Column(nullable = false)
    private String nomeMusica;

    private String compositor;

    private LocalTime tempoMusica;

    private LocalTime introducao;

    @Column(length = 4)
    private String anoLancamento;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    @Enumerated(EnumType.STRING)
    private LancamentoMusica lancamento;

    @ElementCollection(targetClass = Periodos.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "musicas_periodos",
            joinColumns = @JoinColumn(name = "musicas_id"))
    @Column(name = "periodo")
    @Builder.Default
    private List<Periodos> periodos = new ArrayList<>();

    @ElementCollection(targetClass = DiasSemana.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "musicas_diasSemana",
            joinColumns = @JoinColumn(name = "musicas_is"))
    @Column(name = "diasSemana")
    @Builder.Default
    private List<DiasSemana> diasSemana = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Repeticao repetir;

    private int quantidade;

    @Builder.Default
    private Boolean nacional = false;

    private LocalDateTime ultimaExecucao;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Builder.Default
    private Boolean ativa = true;
}
