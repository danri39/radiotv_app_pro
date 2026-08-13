package br.com.drs.radiotv_app_pro.dto;

import br.com.drs.radiotv_app_pro.model.Contrato;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecebimentoDTO {

    private Long id;

    private Contrato contrato;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataVencimento;

    private BigDecimal valorParcela;

    private String numeroFatura;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    private BigDecimal valorEfetivoPago;

    private BigDecimal comissaoVendedor;

    private BigDecimal comissaoAgencia;

    private Boolean pagoComissao;
}
