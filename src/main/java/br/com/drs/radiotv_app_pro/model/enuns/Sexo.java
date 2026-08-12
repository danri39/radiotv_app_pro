package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sexo {

    MASCULINO("Masculino"),
    FEMININO("Feminino"),
    NAOINFORMADO("Não Informado");

    private String descricao;
}
