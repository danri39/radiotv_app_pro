package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sistema {

    RADIOTV("RádioTv"),
    PETSHOP("PetShop"),
    PIZZARIA("Pizzaria"),
    OUTROS("Outros");

    private String descricao;
}
