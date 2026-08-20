package br.com.drs.radiotv_app_pro.model.escritorio;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ferias")
public class Ferias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 8)
    private String chaveUsuario;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFim;

    private Integer quantidadeDias;

    private Integer anoReferenciaAquisitivo;

    private Boolean aprovada;

    private Boolean abonoPecuniario;

    private Integer quantidadeDiasAbono;

    private String motivoRecusa;
}