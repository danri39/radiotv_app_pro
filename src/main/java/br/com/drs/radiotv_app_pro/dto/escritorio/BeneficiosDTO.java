package br.com.drs.radiotv_app_pro.dto.escritorio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiosDTO {

    private Long id;

    private Long funcionarioId;

    private String beneficios;

    private BigDecimal valorFuncionario;

    private BigDecimal valorFamilia;

    private String descontos;

    private BigDecimal valorDesconto;
}