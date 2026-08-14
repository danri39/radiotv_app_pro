package br.com.drs.radiotv_app_pro.dto.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.DiasSemana;
import br.com.drs.radiotv_app_pro.model.enuns.Periodos;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorariosBreaksDTO {

    private Long id;

    private List<Periodos> periodos;

    private List<DiasSemana> diasSemana;

    private Integer breaksPorHora;

    private Integer tempoBreaks;
}