package br.com.drs.radiotv_app_pro.dto;

import br.com.drs.radiotv_app_pro.model.enuns.TipoPessoa;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {

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

    private String pessoaContato;

    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String estado;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInauguracao;

    private Boolean contratosValidos;

    private Long ramoAtividade;

    private Boolean ativo;
}