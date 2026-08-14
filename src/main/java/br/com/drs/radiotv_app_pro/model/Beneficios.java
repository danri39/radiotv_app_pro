package br.com.drs.radiotv_app_pro.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "beneficios")
public class Beneficios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    private String beneficios;

    private BigDecimal valorFuncionario;

    private BigDecimal valorFamilia;

    private String descontos;

    private BigDecimal valorDesconto;
}