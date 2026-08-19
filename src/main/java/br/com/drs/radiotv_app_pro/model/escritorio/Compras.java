package br.com.drs.radiotv_app_pro.model.escritorio;

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

    @Column(nullable = false, length = 8)
    private String chaveUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produtos_id", nullable = false)
    private Produto produtos;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataCompra;

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