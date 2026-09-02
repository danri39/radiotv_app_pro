package br.com.drs.radiotv_app_pro.dto.escritorio;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemCompraDetalhadaDTO {

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate data;

    private String nomeProduto;

    private String solicitadoPorFuncionario;

    private int quantidade;

    private BigDecimal valorTotal;

    private Boolean aprovada;
}