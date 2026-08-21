package br.com.drs.radiotv_app_pro.dto.radio;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PastasDTO {

    private Long id;

    private String arquivos;

    private String chamadas;

    private String comerciais;

    private String componentes;

    private String ferramentas;

    private String horaCerta;

    private String locucao;

    private String mapaIrradiacao;

    private String musicas;

    private String retornoBanco;

    private String roteiroComercial;

    private String roteiroMusical;

    private String trilhas;

    private String vinhetas;
}
