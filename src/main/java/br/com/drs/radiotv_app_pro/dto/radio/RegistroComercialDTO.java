package br.com.drs.radiotv_app_pro.dto.radio;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroComercialDTO {

    private String id;

    private String nomeCliente;

    private String locutor;

    private LocalTime tempo;

    private String tipoComercial; //Institucional ou Promocional

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate inicio;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate termino;

    private String pastaArquivo;

    @Builder.Default
    private Boolean ativo = true;
}
