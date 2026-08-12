package br.com.drs.radiotv_app_pro.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "programas")
public class Programa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomePrograma;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaInicio;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaFinal;

    private Boolean feriados;

    private Boolean breaks;

    private Boolean breaksProprios;

    private Boolean ativo;
}