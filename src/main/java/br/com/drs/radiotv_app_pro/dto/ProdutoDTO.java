package br.com.drs.radiotv_app_pro.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDTO {

    private Long id;

    private String nomeProduto;

    private String descricao;

    private String marca;

    private String quantidade; // ex: "1 Litro", "500g"

    private BigDecimal valorUnitario;

    private Integer quantidadeEstoque;
}