package br.com.drs.radiotv_app_pro.dto.escritorio;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigEscritorioDTO {


    private Long id = 1L;

    // --- 🏦 CONFIGURAÇÕES BANCÁRIAS PADRÃO ---
    private String agenciaPadrao;

    private String contaCorrentePadrao;

    private String carteiraPadrao;

    private String codigoCedentePadrao;

    // Código de Transmissão ou Código do Convênio (Exigido por alguns bancos no CNAB)
    private String codigoConvenioBancario;

    // --- 📝 PARAMETRIZAÇÃO DE BOLETOS (REGRAS DE COBRANÇA) ---
    @Column(name = "percentual_multa_atraso")
    private Double percentualMultaAtraso; // Ex: 2.00 (%)

    @Column(name = "percentual_juros_mes")
    private Double percentualJurosMes; // Ex: 1.00 (%) ao mês

    @Column(name = "dias_para_protesto_automatico")
    private Integer diasParaProtestoAutomatico; // Ex: 5 (dias após o vencimento)

    // --- 📁 FLUXO DE ARQUIVOS CNAB (FATURAMENTO BANCO) ---
    @Column(name = "layout_cnab_padrao")
    private String layoutCnabPadrao; // "CNAB240" ou "CNAB400"

    @Column(name = "sequencial_remessa_atual")
    private Integer sequencialRemessaAtual; // Ex: 1 (O sistema incrementa a cada arquivo gerado: 1, 2, 3...)

    @Column(name = "diretorio_salvar_remessas")
    private String diretorioSalvarRemessas; // Pasta local/rede onde o sistema vai cuspir o arquivo .REM

    @Column(name = "diretorio_ler_retornos")
    private String diretorioLerRetornos; // Pasta onde o usuário vai jogar o arquivo .RET do banco para o sistema ler

    // --- 📜 FLUXO DE NOTA FISCAL (NFS-e / FATURAMENTO FISCAL) ---
    @Column(name = "proximo_numero_nota_fiscal")
    private Long proximoNumeroNotaFiscal; // Guarda o sequencial das notas fiscais da rádio

    @Column(name = "serie_nota_fiscal")
    private String serieNotaFiscal; // Ex: "A", "1" ou "E"

    @Column(name = "aliquota_iss_padrao")
    private Double aliquotaIssPadrao; // Alíquota de ISS do município da rádio (Ex: 2.00 ou 5.00%)

    // --- 📧 COMUNICAÇÃO ---
    private String emailGerenteComercial;

    private String caminhoDiretorioAudiosPlayer;

    private String pastaSalvarRoteiro;
}
