package br.com.drs.radiotv_app_pro.dto.escritorio;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoteiroComercialDTO {

    @JsonProperty("dia_roteiro")
    private String diaRoteiro; 

    private List<BreakRoteiroDTO> breaks;
}