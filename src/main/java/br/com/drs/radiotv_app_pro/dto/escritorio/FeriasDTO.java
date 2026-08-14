package br.com.drs.radiotv_app_pro.dto.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriasDTO {

    private Long id;

    private Funcionario funcionario;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFim;

    private Integer quantidadeDias; // Dias efetivos de folga (Gozo)

    private Integer anoReferenciaAquisitivo; // Ex: 2026

    private Boolean aprovada;

    // NOVOS CAMPOS PARA SUPORTE A ABONO PECUNIÁRIO (VENDA DE FÉRIAS — CLT)Sim
    private Boolean abonoPecuniario; // Identifica se vendeu parte das férias

    private Integer quantidadeDiasAbono; // Máximo 10 dias pela CLT

    // Campo para armazenar o porquê de o RH ter negado o pedido
    private String motivoRecusa;
}
