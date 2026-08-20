package br.com.drs.radiotv_app_pro.dto.escritorio;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VeiculosDTO {

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
