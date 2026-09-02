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
public class FaturamentoDTO {

    private Long id;

    private Long contratoId;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    private BigDecimal valorParcela;

    private Boolean faturado;

    private Boolean paga;

    private LocalDate dataPagamentoReal; // Data em que o cliente realmente pagou

    private String numeroFatura; // Ex: FAT-2026-0001

    private Boolean ativo;
}