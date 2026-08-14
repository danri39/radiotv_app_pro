package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    PAGO("Pago"),
    ATRASADO("Atrasado"),
    NEGATIVADO("Vencido"),
    EXPIRADO("Espirado"),
    PRESCRITO("Prescrito"),
    OUTROS("Outros");

    private String descricao;

}
