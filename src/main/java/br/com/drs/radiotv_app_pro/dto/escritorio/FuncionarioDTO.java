package br.com.drs.radiotv_app_pro.dto.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.Formacao;
import br.com.drs.radiotv_app_pro.model.enuns.Sexo;
import br.com.drs.radiotv_app_pro.model.enuns.TipoPessoa;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FuncionarioDTO {

    private Long id;

    private String chaveUsuario;

    private String nome;

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
    private LocalDate dataNascimento;

    private Sexo sexo;

    private Formacao formacao;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate admissao;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate demissao;

    private BigDecimal salario;

    private String pisPasep;

    private String banco;

    private String agencia;

    private String conta;

    @Builder.Default
    private Boolean ativo = true;
}