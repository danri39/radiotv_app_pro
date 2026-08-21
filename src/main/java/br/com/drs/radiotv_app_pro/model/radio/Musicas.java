package br.com.drs.radiotv_app_pro.model.radio;

import br.com.drs.radiotv_app_pro.model.enuns.*;
import br.com.drs.radiotv_app_pro.model.escritorio.Programa;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "musicas")
public class Musicas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String artista;

    private String nomeMusica;

    private String compositor;

    private LocalTime tempoMusica;

    private LocalTime tempoIntroducao;

    private Boolean nacional;

    @Enumerated(EnumType.STRING)
    private Repeticao repeticao;

    private int quantidade;

    @Column(length = 4)
    private String anoLancamento;

    @ElementCollection(targetClass = Periodos.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "musicas_periodos", joinColumns = @JoinColumn(name = "musicasId"))
    @Enumerated(EnumType.STRING)
    @Column(name = "periodo")
    private List<Periodos> periodos; //

    @ElementCollection(targetClass = DiasSemana.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "musicas_dias", joinColumns = @JoinColumn(name = "musicasId"))
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana")
    private List<DiasSemana> diasSemana;

    @ManyToOne
    @JoinColumn(name = "programa_id")
    private Programa programa;

    @Enumerated(EnumType.STRING)
    private EstiloMusica estiloMusical;

    @Enumerated(EnumType.STRING)
    private TipoEstrutura tipoEstrutura;

    @Enumerated(EnumType.STRING)
    private LancamentoMusica lancamentoMusical;

    private String observacao;

    @Builder.Default
    private Boolean ativo = true ;
}
