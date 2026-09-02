package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.GerencialDTO;
import br.com.drs.radiotv_app_pro.repository.escritorio.GerencialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GerencialService {

    private final GerencialRepository repository;

    public GerencialDTO obterPainelComercial(int mes, int ano) {
        Integer vendedoresAtivos = repository.countVendedoresAtivosMes(mes, ano);
        if (vendedoresAtivos == null || vendedoresAtivos == 0) {
            vendedoresAtivos = 1; // Fallback se houver apenas o operador
        }

        BigDecimal faturamento = repository.somarFaturamentoMes(mes, ano);
        if (faturamento == null) faturamento = BigDecimal.ZERO;

        Integer contratosFechados = repository.countContratosFechadosMes(mes, ano);
        if (contratosFechados == null) contratosFechados = 0;

        BigDecimal comissoesLiquidadas = repository.somarComissoesLiquidadasMes(mes, ano);
        if (comissoesLiquidadas == null) comissoesLiquidadas = BigDecimal.ZERO;

        BigDecimal comissoesPendentes = repository.somarComissoesPendentes();
        if (comissoesPendentes == null) comissoesPendentes = BigDecimal.ZERO;

        // Melhor Vendedor do Período
        String melhorVendedor = "Nenhum no período";
        BigDecimal totalMelhorVendedor = BigDecimal.ZERO;
        List<Object[]> resultadoMelhor = repository.findMelhorVendedorMes(mes, ano);
        if (!resultadoMelhor.isEmpty()) {
            Object[] linha = resultadoMelhor.get(0);
            melhorVendedor = (String) linha[0];
            if (linha[1] instanceof Number num) {
                totalMelhorVendedor = BigDecimal.valueOf(num.doubleValue());
            }
        }

        // Média de Vendas por Vendedor
        BigDecimal mediaVendasPorVendedor = BigDecimal.ZERO;
        if (vendedoresAtivos > 0 && faturamento.compareTo(BigDecimal.ZERO) > 0) {
            mediaVendasPorVendedor = faturamento.divide(BigDecimal.valueOf(vendedoresAtivos), 2, RoundingMode.HALF_UP);
        }

        // Ticket Médio por Contrato/Fatura
        BigDecimal ticketMedio = BigDecimal.ZERO;
        if (contratosFechados > 0 && faturamento.compareTo(BigDecimal.ZERO) > 0) {
            ticketMedio = faturamento.divide(BigDecimal.valueOf(contratosFechados), 2, RoundingMode.HALF_UP);
        }

        return GerencialDTO.builder()
                .totalVendedoresAtivos(vendedoresAtivos)
                .melhorVendedor(melhorVendedor)
                .totalVendidoMelhorVendedor(totalMelhorVendedor)
                .mediaVendasPorVendedor(mediaVendasPorVendedor)
                .faturamentoRealizado(faturamento)
                .totalContratosFechados(contratosFechados)
                .ticketMedio(ticketMedio)
                .totalComissoesLiquidadas(comissoesLiquidadas)
                .totalComissoesPrevistas(comissoesPendentes)
                .build();
    }
} 