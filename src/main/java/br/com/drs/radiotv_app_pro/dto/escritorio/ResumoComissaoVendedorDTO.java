package br.com.drs.radiotv_app_pro.dto.escritorio;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumoComissaoVendedorDTO {

    private String chaveUsuario;

    private int totalFaturasPagas;

    private BigDecimal valorTotalFaturas;

    private BigDecimal valorTotalComissao;

    private List<ComissaoDTO> detalhes;
}