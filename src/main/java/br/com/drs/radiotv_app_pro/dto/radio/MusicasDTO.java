package br.com.drs.radiotv_app_pro.dto.radio;

import br.com.drs.radiotv_app_pro.model.enuns.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicasDTO {

    private Long id;

    private String artista;

    private String nomeMusica;

    private String compositor;

    private String tempoMusica;

    private String introducao;       

    private String anoLancamento;

    private Genero genero;

    private LancamentoMusica lancamento;

    private List<Periodos> periodos;

    private List<DiasSemana> diasSemana;

    private Repeticao repetir;

    private Integer quantidade;

    private Boolean nacional;

    private LocalDateTime ultimaExecucao;

    private String observacao;

    private Boolean ativa;
}
