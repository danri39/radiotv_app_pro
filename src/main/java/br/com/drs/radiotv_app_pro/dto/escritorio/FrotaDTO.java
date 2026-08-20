package br.com.drs.radiotv_app_pro.dto.escritorio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FrotaDTO {

    private Long id;

    private Long veiculosId;

    private String chaveUsuario;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate saida;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate chegada;

    private Double kmSaida;

    private Double kmChegada;

    private Double kmAndado;

    private String ocorrenciaSaida;

    private String ocorrenciaChegada;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate ultimaRevisao;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate proximaRevisao;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataAbastecimento;

    private Double quantidadeLitros;

    // Campo calculado - não persistido
    private Double consumoKmPorLitro;
}