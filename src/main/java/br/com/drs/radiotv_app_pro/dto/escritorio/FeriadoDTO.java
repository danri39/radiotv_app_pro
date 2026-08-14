package br.com.drs.radiotv_app_pro.dto.escritorio;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeriadoDTO {

    private Long id;

    private String descricao;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFeriado;
}
