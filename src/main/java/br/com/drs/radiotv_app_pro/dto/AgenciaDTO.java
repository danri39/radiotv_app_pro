package br.com.drs.radiotv_app_pro.dto;

import br.com.drs.radiotv_app_pro.model.enuns.TipoPessoa;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgenciaDTO {

    private Long id;

    private String razaoSocial;

    private String nomeFantasia;

    private TipoPessoa tipoPessoa;

    private String cpf;

    private String cnpj;

    private String rg;

    private String inscricao;

    private String email;

    private String telefone;

    private String celular;

    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String estado;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInauguracao;

    private String banco;

    private String agencia;

    private String conta;

    private int comissaoVendas;

    private BigDecimal vendasMes;

    private BigDecimal comissaoMes;

    private Boolean contratosValidos;

    private Boolean ativo = true;
}