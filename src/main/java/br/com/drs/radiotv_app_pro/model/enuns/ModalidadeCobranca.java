package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModalidadeCobranca {

    VALOR_FIXO("Valor Fixo"),
    COPARTICIPACAO("Coparticipação"),
    CONSUMO_VARIAVEL("Consumo Variável"),
    PERCENTUAL_SALARIO("Percentual Salário");

    private String descricao;
}
