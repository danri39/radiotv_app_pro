package br.com.drs.radiotv_app_pro.model;

import br.com.drs.radiotv_app_pro.model.enuns.TipoPessoa;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "agencia")
public class Agencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String razaoSocial;

    @Column(nullable = false, length = 150)
    private String nomeFantasia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPessoa tipoPessoa;

    @Column(unique = true, length = 14)
    private String cpf;

    @Column(unique = true, length = 14)
    private String cnpj;

    @Column(length = 20)
    private String rg;

    @Column(length = 20)
    private String inscricao;

    // --- CONTATO (Novos) ---
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(length = 21)
    private String celular;

    @Column(length = 9)
    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    @Column(length = 2)
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

    @Builder.Default
    private Boolean ativo = true;
}