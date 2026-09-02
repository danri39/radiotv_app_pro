package br.com.drs.radiotv_app_pro.model.escritorio;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "veiculos")
public class Veiculos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String marca;

    private String modelo;

    private String placa;

    private String chassi;

    private String renavan;

    private String cor;

    private String anoFabricacao;

    private Long kilometragemInicio;

    @Builder.Default
    private Boolean ativo = true;
}
