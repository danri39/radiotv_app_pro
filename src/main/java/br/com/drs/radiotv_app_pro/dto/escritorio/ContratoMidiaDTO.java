package br.com.drs.radiotv_app_pro.dto.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.DiasSemana;
import br.com.drs.radiotv_app_pro.model.enuns.Distribuicao;
import br.com.drs.radiotv_app_pro.model.enuns.TempoMidia;
import br.com.drs.radiotv_app_pro.model.enuns.TipoMidia;
import br.com.drs.radiotv_app_pro.model.escritorio.ContratoMidiaAudioPool;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContratoMidiaDTO {

    private Long id;

    private Long contratoId;

    private TipoMidia tipoMidia;

    private String identificacao;

    private int quantidade;

    private TempoMidia tempoMidia;

    private List<DiasSemana> diasSemana;

    private Distribuicao distribuicao;

    private LocalTime horarioEspecifico;

    private Long programaId;

    private Long ramoAtividadeId;

    private Integer prioridade;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFinal;

    private Boolean ativo;

    private Boolean temMultiplosAudios = false;

    private List<ContratoMidiaAudioPool> audiosPool;
}