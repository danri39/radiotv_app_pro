package br.com.drs.radiotv_app_pro.dto.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.DiasSemana;
import br.com.drs.radiotv_app_pro.model.enuns.TipoPrograma;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramaDTO {

    private Long id;
    private String nomePrograma;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaInicio;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaFinal;

    private List<DiasSemana> diasSemana;
    private TipoPrograma tipoPrograma;

    @JsonProperty("nacional")
    private Boolean nacional;

    @JsonProperty("feriados")
    private Boolean feriados;

    @JsonProperty("breaks")
    private Boolean breaks;

    @JsonProperty("breaksProprios")
    private Boolean breaksProprios;

    @JsonProperty("ativo")
    private Boolean ativo;
}
