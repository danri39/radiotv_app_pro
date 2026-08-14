package br.com.drs.radiotv_app_pro.dto.escritorio;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalTime;

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

    private Boolean feriados;

    private Boolean breaks;

    private Boolean breaksProprios;

    private Boolean ativo;
}
