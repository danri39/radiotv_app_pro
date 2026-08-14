package br.com.drs.radiotv_app_pro.dto.escritorio;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolhaPagamentoDTO {

    private Long id;

    private Long funcionarioId;

    private String nomeFuncionario;

    private String mesAno;

    private BigDecimal salarioBruto;

    private BigDecimal descontoInss;

    private BigDecimal descontoIrrf;

    private BigDecimal descontoBeneficios;

    private BigDecimal totalDescontos;

    private BigDecimal salarioLiquido;

    private Boolean fechada;
}