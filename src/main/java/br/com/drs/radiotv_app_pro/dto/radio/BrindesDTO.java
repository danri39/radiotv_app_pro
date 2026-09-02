package br.com.drs.radiotv_app_pro.dto.radio;

import br.com.drs.radiotv_app_pro.model.radio.Ouvintes;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrindesDTO {

    private Long id;

    private String descricao;

    private String identificacao;

    private LocalDate dtSorteio;

    private Ouvintes ouvintes;

    private LocalDate dtBuscarBrinde;
}
