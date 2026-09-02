package br.com.drs.radiotv_app_pro.dto.escritorio;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministracaoDTO {

    private Long id;

    //vendas
    private BigDecimal valorContratosHoje;

    private BigDecimal valorContratosSemana;

    private BigDecimal valorContratosMes;

    private BigDecimal valorParcelasMes;

    private BigDecimal valorParcelasProximoMes;

    //recebimentos
    private BigDecimal valorRecebidoHoje;

    private BigDecimal valorRecebidoSemana;

    private BigDecimal valorRecebidoMes;

    private BigDecimal valorParcelasFaturadas;

    private BigDecimal valorParcelasAtrasadas;

    //pagamentos
    private BigDecimal valorPagamentosHoje;

    private BigDecimal valorPagamentosSemana;

    private BigDecimal valorPagamentosMes;

    private BigDecimal valorPagamentosProximoMes;

    private BigDecimal valorPagamentosAtrasadas;

    //frota
    private int kilometrosPercorridosHoje;

    private int kilometrosPercorridosSemana;

    private int kilometrosPercorridosMes;

    //salarios
    private BigDecimal valorTotalSalarios;

    private BigDecimal valorTotalComissao;

    private BigDecimal valorTotalEncargos;

    private BigDecimal valorTotalFerias;

    private BigDecimal valorTotalBeneficios;

    private BigDecimal valorTotalMes;

    private BigDecimal valorTotalMesAnterior;

    private BigDecimal valorTotalPrevisaoProximoMes;
}
