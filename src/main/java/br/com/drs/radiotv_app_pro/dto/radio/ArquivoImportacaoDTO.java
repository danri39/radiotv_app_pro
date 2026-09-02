package br.com.drs.radiotv_app_pro.dto.radio;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoImportacaoDTO {

    private String nomeArquivo;
    private String conteudoXml;
    private Integer totalMusicas;
    private Integer musicasImportadas;
    private Integer musicasComErro;
    private List<String> erros;
}