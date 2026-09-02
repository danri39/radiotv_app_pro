package br.com.drs.radiotv_app_pro.dto.escritorio;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaBancariaDTO {

    private Long id;

    private String nomeBanco;

    private String codigoBanco;

    private String agencia;

    private String digitoAgencia;

    private String contaCorrente;

    private String digitoConta;

    private String codigoCedente;

    private String carteira;
}