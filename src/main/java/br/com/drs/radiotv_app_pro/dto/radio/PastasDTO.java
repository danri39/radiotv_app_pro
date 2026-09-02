package br.com.drs.radiotv_app_pro.dto.radio;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PastasDTO {

    private Long id = 1L;

    private String arquivos;

    private String chamadas;

    private String comerciais;

    private String componentes;

    private String ferramentas;

    private String horaCerta;

    private String locucao;

    private String mapasIrradiacao;

    private String musicas;

    private String retornoBanco;

    private String roteiroComercial;

    private String roteiroMusical;

    private String trilhas;

    private String vinhetas;
}
