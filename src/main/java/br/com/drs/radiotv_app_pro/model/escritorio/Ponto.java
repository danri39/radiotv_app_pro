package br.com.drs.radiotv_app_pro.model.escritorio;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ponto")
public class Ponto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    private LocalDateTime horaEntrada;

    private LocalDateTime horaSaidaIntervalo;

    private LocalDateTime horaEntradaIntervalo;

    private LocalDateTime horaSaida;

    private LocalDateTime horaEntradaExtra;

    private LocalDateTime horaSaidaExtra;

    private LocalTime horaExtraDia;
    private String horaExtraMes;

    // --- NOVOS CAMPOS PARA CONTROLE DE INCONSISTÊNCIAS E REVISÃO ---
    private Boolean possuiInconsistencia = false;
    private String descricaoInconsistencia; // Ex: "Esqueceu de bater a saída do intervalo"

    private Boolean enviadoParaCorrecao = false;
    private String observacaoEscritorio; // Recado do RH para o funcionário saber o que corrigir
}