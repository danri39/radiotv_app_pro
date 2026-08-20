package br.com.drs.radiotv_app_pro.dto.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Agencia;
import br.com.drs.radiotv_app_pro.model.escritorio.Contrato;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComissaoDTO {

    private Long id;

    private Contrato contrato;

    private Agencia agencia;

    private String chaveUsuario;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamentoReal;

    private BigDecimal valorParcela;

    private String numeroFatura;

    private BigDecimal valorComissaoAgencia;

    private BigDecimal valorComissaoVendedor;

    private String numeroNotaAgencia;

    @Builder.Default
    private Boolean pagaAgencia = false;

    @Builder.Default
    private Boolean pagaVendedor = false;

    @Builder.Default
    private Boolean ativo = true;
}
