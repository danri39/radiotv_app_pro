package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Formacao {

    FUNDAMENTAL("Educação Fundamental"),
    MEDIO("Educação Média"),
    GRADUACAO("Graduação"),
    POSGRADUACAO("Pós Graduação"),
    MESTRADO("Mestrado"),
    DOUTORADO("Doutorado"),
    POSDOUTORADO("Pós Doutorado"),
    NAOINFORMADO("Não Informado");

    private String descricao;
}
