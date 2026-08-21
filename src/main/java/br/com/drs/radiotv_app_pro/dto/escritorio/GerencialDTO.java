package br.com.drs.radiotv_app_pro.dto.escritorio;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GerencialDTO {

    private Long id;

    // Equipe e Médias
    private Integer totalVendedoresAtivos;
    private String melhorVendedor;
    private BigDecimal totalVendidoMelhorVendedor;
    private BigDecimal mediaVendasPorVendedor;

    // Metas & Faturamento
    private BigDecimal faturamentoRealizado;
    private BigDecimal metaGlobalMes;
    private BigDecimal percentualMetaAtingida;
    private Integer vendedoresAcimaDaMeta;

    // Eficiência Comercial & Ticket
    private Integer totalContratosFechados;
    private Integer totalPropostasAbertas;
    private BigDecimal taxaConversao;
    private BigDecimal ticketMedio;

    // Comissões & Projeções
    private BigDecimal totalComissoesLiquidadas;
    private BigDecimal totalComissoesPrevistas;
    private BigDecimal valorPipelineNegociacao;

    // Carteira
    private Integer novosClientes;
    private Integer renovacoesContrato;
}
