package br.com.drs.radiotv_app_pro.model.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.DiasSemana;
import br.com.drs.radiotv_app_pro.model.enuns.Distribuicao;
import br.com.drs.radiotv_app_pro.model.enuns.TempoMidia;
import br.com.drs.radiotv_app_pro.model.enuns.TipoMidia;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
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
@Entity
@Table(name = "contrato_midia")
public class ContratoMidia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @Enumerated(EnumType.STRING)
    private TipoMidia tipoMidia;

    private String identificacao;

    private int quantidade;

    @Enumerated(EnumType.STRING)
    private TempoMidia tempoMidia;

    private List<DiasSemana> diasSemana;

    @Enumerated(EnumType.STRING)
    private Distribuicao distribuicao;

    private LocalTime horarioEspecifico;

    @ManyToOne
    @JoinColumn(name = "programa_id")
    private Programa programa;

    @ManyToOne
    @JoinColumn(name = "ramo_atividade_id")
    private RamoAtividade ramoAtividade;

    private Integer prioridade;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFinal;

    private Boolean ativo;

    // ADICIONADO: Define se o contrato vai usar múltiplos áudios em rodízio
    private Boolean temMultiplosAudios = false;

    // ADICIONADO: Lista dinâmica dos áudios com suas respectivas inserções/pesos
    @ElementCollection
    @CollectionTable(name = "contrato_midia_audio_pool", joinColumns = @JoinColumn(name = "contrato_midia_id"))
    private List<ContratoMidiaAudioPool> audiosPool;
}