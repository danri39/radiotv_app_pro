package br.com.drs.radiotv_app_pro.dto.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PontoDTO {

    private Long id;

    private Funcionario funcionario;

    private LocalDateTime horaEntrada;

    private LocalDateTime horaSaidaIntervalo;

    private LocalDateTime horaEntradaIntervalo;

    private LocalDateTime horaSaida;

    private LocalDateTime horaEntradaExtra;

    private LocalDateTime horaSaidaExtra;

    private LocalTime horaExtraDia;

    private String horaExtraMes; // Alterado para String para suportar somas maiores que 24 horas

    // --- NOVOS CAMPOS PARA CONTROLE DE INCONSISTÊNCIAS E REVISÃO ---
    private Boolean possuiInconsistencia = false;

    private String descricaoInconsistencia; // Ex: "Esqueceu de bater a saída do intervalo"

    private Boolean enviadoParaCorrecao = false;

    private String observacaoEscritorio; // Recado do RH para o funcionário saber o que corrigir
}