package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoPessoa {

    FISICA("Fisica"),
    JURIDICA("Juridica");

    private String descricao;
}
