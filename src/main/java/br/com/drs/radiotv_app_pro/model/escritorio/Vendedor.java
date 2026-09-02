package br.com.drs.radiotv_app_pro.model.escritorio;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vendedor")
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String chaveUsuario;

    private BigDecimal metaMes;

    @Column(length = 7)
    private String mesAno;

    private BigDecimal vendasMes;

    private BigDecimal vendasTotal;

    private int comissaoVendas;
}
