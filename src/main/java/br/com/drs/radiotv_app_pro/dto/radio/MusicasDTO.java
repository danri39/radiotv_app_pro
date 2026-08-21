package br.com.drs.radiotv_app_pro.dto.radio;

import br.com.drs.radiotv_app_pro.model.enuns.*;
import br.com.drs.radiotv_app_pro.model.escritorio.Programa;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MusicasDTO {

    private Long id;

    private String artista;

    private String nomeMusica;

    private String compositor;

    private LocalTime tempoMusica;

    private LocalTime tempoIntroducao;

    private Boolean nacional;

    private Repeticao repeticao;

    private int quantidade;

    private String anoLancamento;

    private List<Periodos> periodos;

    private List<DiasSemana> diasSemana;

    private Programa programa;

    private EstiloMusica estiloMusical;

    private TipoEstrutura tipoEstrutura;

    private LancamentoMusica lancamentoMusical;

    private String observacao;

    @Builder.Default
    private Boolean ativo = true ;
}
