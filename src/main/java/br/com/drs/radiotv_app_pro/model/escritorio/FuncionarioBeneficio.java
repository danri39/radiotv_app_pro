package br.com.drs.radiotv_app_pro.model.escritorio;

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
@Table(name = "funcionario_beneficios")
public class FuncionarioBeneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficio_id", nullable = false)
    private Beneficios beneficio;

    @Builder.Default
    private Integer quantidadeDependentes = 0;

    private BigDecimal valorTitularCalculado;

    private BigDecimal valorDependentesCalculado;

    private BigDecimal valorTotalDesconto;

    private LocalDate dataAdesao;

    @Builder.Default
    private Boolean ativo = true;

    private String observacao;
}