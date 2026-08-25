package br.com.drs.radiotv_app_pro.model.radio;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "registro_comercial")
public class RegistroComercial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String nomeCliente;

    private String locutor;

    @JsonFormat(pattern = "mm:ss")
    private LocalTime tempo;

    private String tipoComercial; //Institucional ou Promocional

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate inicio;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate termino;

    private String pastaArquivo;

    @Builder.Default
    private Boolean ativo = true;
}
