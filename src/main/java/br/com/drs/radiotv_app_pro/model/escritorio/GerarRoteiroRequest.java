package br.com.drs.radiotv_app_pro.model.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.DiasSemana;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GerarRoteiroRequest {

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate data;

    private DiasSemana diaSemana;
}