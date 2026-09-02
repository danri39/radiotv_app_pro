package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Repeticao {

    HORAS("Horas"),
    DIAS("Dias"),
    MESES("Meses"),
    ANOS("Anos");

    private String descricao;
}
