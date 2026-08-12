package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Papel {

    SUPERUSUARIO("Super Usuário"),
    ADMINISTRADOR("Administrador"),
    GERENTE("Gerente"),
    USUARIO("Usuário"),
    CONVIDADO("Convidado"),
    CLIENTE("Cliente"),
    AGENCIA("Agência");

    private String descricao;
}