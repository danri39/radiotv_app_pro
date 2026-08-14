package br.com.drs.radiotv_app_pro.dto.escritorio;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoDTO {

    private Long id;

    private Long clienteId;
    private String clienteNomeFantasia;

    private Long vendedorId;

    private Long agenciaId;
    private String agenciaNomeFantasia;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFinal;

    private BigDecimal valorTotal;

    private Integer quantidadeParcelas;

    private List<ContratoMidiaDTO> midias;

    private List<ContratoPagamentoDTO> pagamentos;

    private Boolean ativo;
}