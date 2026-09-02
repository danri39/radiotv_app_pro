package br.com.drs.radiotv_app_pro.dto.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoDTO {

    private Long id;

    private String descricao;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataVencimento;

    private BigDecimal valor;

    private String numeroDocumento;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    private BigDecimal valorPagamentoEfetivo;

    private Status status;

    @Builder.Default
    private Boolean ativo = true;
}
