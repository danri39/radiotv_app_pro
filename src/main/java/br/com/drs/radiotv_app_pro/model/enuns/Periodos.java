package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Periodos {

    MADRUGADA("Madrugada"),
    MANHA("Manhã"),
    TARDE("Tarde"),
    NOITE("Noite"),
    TODOS("Todos");

    private String descricao;
}
