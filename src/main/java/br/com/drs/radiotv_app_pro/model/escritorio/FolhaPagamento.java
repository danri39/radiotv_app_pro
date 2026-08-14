package br.com.drs.radiotv_app_pro.model.escritorio;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "folha_pagamento")
public class FolhaPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(nullable = false, length = 7)
    private String mesAno;

    @Column(nullable = false)
    private BigDecimal salarioBruto;

    @Column(nullable = false)
    private BigDecimal descontoInss;

    @Column(nullable = false)
    private BigDecimal descontoIrrf;

    @Column(nullable = false)
    private BigDecimal descontoBeneficios; // Soma de valorFuncionario + valorFamilia + valorDesconto da tabela de benefícios

    @Column(nullable = false)
    private BigDecimal totalDescontos; // Soma de INSS + IRRF + Benefícios

    @Column(nullable = false)
    private BigDecimal salarioLiquido; // Salário Bruto - Total Descontos

    @Builder.Default
    private Boolean fechada = false; // Se a folha já foi processada e fechada de forma definitiva
}