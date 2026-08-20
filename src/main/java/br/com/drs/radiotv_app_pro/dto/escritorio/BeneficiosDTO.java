package br.com.drs.radiotv_app_pro.dto.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.ModalidadeCobranca;
import br.com.drs.radiotv_app_pro.model.enuns.TipoBeneficio;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiosDTO {

    private Long id;

    private String nomeBeneficio;

    private TipoBeneficio tipo;

    private ModalidadeCobranca modalidadeCobranca;

    private BigDecimal valorFuncionario;

    private BigDecimal valorFamilia;

    private BigDecimal percentualCoparticipacao; 

    private String observacoes;

    @Builder.Default
    private Boolean ativo = true;
}