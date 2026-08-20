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
public class FuncionarioBeneficioDTO {

    private Long id;

    private Long funcionarioId;

    private String nomeFuncionario;

    private Long beneficioId;

    private String nomeBeneficio;

    private String modalidadeCobranca;

    private Integer quantidadeDependentes;

    private BigDecimal valorTitularCalculado;

    private BigDecimal valorDependentesCalculado;

    private BigDecimal valorTotalDesconto;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataAdesao;

    private Boolean ativo;

    private String observacao;
}