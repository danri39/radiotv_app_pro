package br.com.drs.radiotv_app_pro.dto;

import br.com.drs.radiotv_app_pro.model.Funcionario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VendedorDTO {

    private Long id;

    private Funcionario funcionario;

    private BigDecimal metaMes;

    private String mesAno;

    private BigDecimal vendasMes;

    private BigDecimal vendasTotal;

    private int comissaoVendas;
}
