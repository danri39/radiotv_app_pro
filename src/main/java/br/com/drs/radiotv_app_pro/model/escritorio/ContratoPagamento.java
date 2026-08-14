package br.com.drs.radiotv_app_pro.model.escritorio;

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
@Table(name = "contrato_pagamento")
public class ContratoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    private BigDecimal valorParcela;

    private Boolean faturado;

    private Boolean paga;

    private LocalDate dataPagamentoReal; // Data em que o cliente realmente pagou

    private String numeroFatura; // Ex: FAT-2026-0001

    private Boolean ativo;
}