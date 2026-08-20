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
@Table(name = "comissao")
public class Comissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agencia_id")
    private Agencia agencia;

    @Column(nullable = false, length = 8)
    private String chaveUsuario;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamentoReal;

    private BigDecimal valorParcela;

    private String numeroFatura;

    private BigDecimal valorComissaoAgencia;

    private BigDecimal valorComissaoVendedor;

    private String numeroNotaAgencia;

    @Builder.Default
    private Boolean pagaAgencia = false;

    @Builder.Default
    private Boolean pagaVendedor = false;

    @Builder.Default
    private Boolean ativo = true;
}
