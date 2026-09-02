package br.com.drs.radiotv_app_pro.dto.escritorio;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriasDTO {

    private Long id;

    private String chaveUsuario;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFim;

    private Integer quantidadeDias; // Dias efetivos de folga (Gozo)

    private Integer anoReferenciaAquisitivo; // Ex: 2026

    private Boolean aprovada;

    private Boolean abonoPecuniario; // Identifica se vendeu parte das férias

    private Integer quantidadeDiasAbono; // Máximo 10 dias pela CLT

    private String motivoRecusa;

    private String alertaConcessivo;
}
