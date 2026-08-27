package br.com.drs.radiotv_app_pro.model.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.DiasSemana;
import br.com.drs.radiotv_app_pro.model.enuns.TipoPrograma;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "programa_semana", joinColumns = @JoinColumn(name = "programa_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana")
    private List<DiasSemana> diasSemana;

    @Enumerated(EnumType.STRING)
    private TipoPrograma tipoPrograma;

    private Boolean nacional;

    private Boolean feriados;

    private Boolean breaks;

    private Boolean breaksProprios;

    private Boolean ativo;
}