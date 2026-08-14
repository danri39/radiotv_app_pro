package br.com.drs.radiotv_app_pro.dto.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.ContratoMidiaAudioPool;
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

    private Long contratoId;       // TEM QUE ESTAR EXATAMENTE ASSIM

    private String tipoMidia;

    private String identificacao;

    private int quantidade;

    private String tempoMidia;

    private List<String> diasSemana;

    private String distribuicao;

    private LocalTime horarioEspecifico;

    private Long programaId;       // TEM QUE ESTAR EXATAMENTE ASSIM

    private Long ramoAtividadeId;  // TEM QUE ESTAR EXATAMENTE ASSIM

    private Integer prioridade;

    private LocalDate dataInicio;

    private LocalDate dataFinal;

    private Boolean ativo;

    private Boolean temMultiplosAudios;

    private List<ContratoMidiaAudioPool> audiosPool;
}