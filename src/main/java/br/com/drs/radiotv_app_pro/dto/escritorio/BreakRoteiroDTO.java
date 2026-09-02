package br.com.drs.radiotv_app_pro.dto.escritorio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BreakRoteiroDTO {

    private String horario;

    @JsonProperty("capacidade_segundos")
    private Integer capacidadeSegundos;

    @JsonProperty("tempo_usado_segundos")
    private Integer tempoUsadoSegundos;

    private String programa;

    private String observacao;

    private List<String> comerciais;
}