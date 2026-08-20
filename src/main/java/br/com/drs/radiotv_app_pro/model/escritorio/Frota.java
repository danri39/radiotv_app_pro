package br.com.drs.radiotv_app_pro.model.escritorio;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "frota") // Nome mais adequado
public class Frota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculos_id")
    private Veiculos veiculos;

    @Column(nullable = false, length = 8)
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

    @Column(nullable = false)
    @Min(1) // Garante que não seja zero
    private Double quantidadeLitros; // Use Double para maior precisão

    // Método utilitário para calcular consumo
    @Transient
    public Double getKmAndado() {
        if (kmSaida != null && kmChegada != null) {
            return kmChegada - kmSaida;
        }
        return kmAndado;
    }
}