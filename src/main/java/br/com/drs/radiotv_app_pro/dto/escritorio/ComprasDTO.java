package br.com.drs.radiotv_app_pro.dto.escritorio;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprasDTO {

    private Long id;

    private String chaveUsuario;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataCompra;

    private Long produtoId;

    private int quantidade;

    private BigDecimal valorTotal;

    private BigDecimal valorCompra;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    private Boolean paga;

    private Boolean compraAceita;

    private String justificativaRecusa;

    private String chaveAdministrador;

    private Boolean ativa;
}