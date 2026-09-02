package br.com.drs.radiotv_app_pro.dto.radio;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OuvintesDTO {

    private Long id;

    private String nome;

    private String nmTelefone;

    private String cidade;
}
