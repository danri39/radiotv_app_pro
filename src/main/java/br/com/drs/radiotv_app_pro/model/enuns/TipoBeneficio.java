package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoBeneficio {

    SAUDE("Saúde"),
    ODONTO("Odonto"),
    FARMACIA("Farmácia"),
    ALIMENTACAO("Alimentação"),
    TRANSPORTE("Transporte"),
    SEGURO("Seguro"),
    OUTROS("Outros");

    private String descricao;

}
