package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoEstrutura {

    Solo("Solo"),
    Dupla("Dupla"),
    Trio("Trio"),
    Grupo("Grupo");

    private String descricao;
}