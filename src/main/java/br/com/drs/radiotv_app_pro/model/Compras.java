package br.com.drs.radiotv_app_pro.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compras")
public class Compras {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false)
    private Funcionario funcionario;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataCompra;

    @OneToOne
    @JoinColumn(nullable = false)
    private Produto produtos;

    private int quantidade;

    private BigDecimal valorTotal;

    private BigDecimal valorCompra;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    private Boolean paga;

    private Boolean CompraAceita;

    private String justificativaRecusa;

    private String chaveAdministrador;

    private Boolean ativa;
}