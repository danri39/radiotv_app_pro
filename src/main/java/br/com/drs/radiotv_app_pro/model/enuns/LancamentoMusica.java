package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LancamentoMusica {

    LANCAMENTO("Lançamento"),
    RECENTE("Recente"),
    MIDBACK("MidBack"),
    FLASHBACK("FlashBack"),
    CLASSICA("Classica");

    private String descricao;

}
