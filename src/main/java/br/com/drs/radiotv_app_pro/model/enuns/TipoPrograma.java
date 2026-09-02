package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoPrograma {

    MUSICAL("Musical"),
    JORNALISMO("Jornalismo"),
    ESPORTES("Esportes"),
    OUTROS("Outros");

    private String descricao;
}
