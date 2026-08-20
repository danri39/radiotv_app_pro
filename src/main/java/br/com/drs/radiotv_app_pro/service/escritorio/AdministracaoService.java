package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.AdministracaoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Compras;
import br.com.drs.radiotv_app_pro.model.escritorio.Contrato;
import br.com.drs.radiotv_app_pro.model.escritorio.Faturamento;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.model.escritorio.Pagamento;
import br.com.drs.radiotv_app_pro.repository.escritorio.ComprasRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.ContratoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FaturamentoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministracaoService {

    private final ContratoRepository contratoRepository;
    private final FaturamentoRepository faturamentoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PagamentoRepository pagamentoRepository;
    private final ComprasRepository comprasRepository;

    private static final DateTimeFormatter DATE_FORMATTER_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional(readOnly = true)
    public AdministracaoDTO calcularResumoGeral() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.minusDays(hoje.getDayOfWeek().getValue() - 1);
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

        LocalDate inicioProximoMes = hoje.plusMonths(1).withDayOfMonth(1);
        LocalDate fimProximoMes = inicioProximoMes.withDayOfMonth(inicioProximoMes.lengthOfMonth());

        List<Contrato> contratos = contratoRepository.findAll();
        List<Faturamento> faturamentos = faturamentoRepository.findAll();
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        List<Pagamento> pagamentos = pagamentoRepository.findAll();
        List<Compras> compras = comprasRepository.findAll();

        // ==========================================
        // 1. VENDAS & CONTRATOS
        // ==========================================
        BigDecimal valorContratosHoje = contratos.stream()
                .filter(c -> c.getDataInicio() != null && c.getDataInicio().isEqual(hoje))
                .map(c -> c.getValorTotal() != null ? c.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorContratosSemana = contratos.stream()
                .filter(c -> c.getDataInicio() != null && !c.getDataInicio().isBefore(inicioSemana) && !c.getDataInicio().isAfter(hoje))
                .map(c -> c.getValorTotal() != null ? c.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorContratosMes = contratos.stream()
                .filter(c -> c.getDataInicio() != null && !c.getDataInicio().isBefore(inicioMes) && !c.getDataInicio().isAfter(fimMes))
                .map(c -> c.getValorTotal() != null ? c.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorParcelasMes = faturamentos.stream()
                .filter(f -> {
                    LocalDate dt = parseData(String.valueOf(f.getDataPagamento()));
                    return dt != null && !dt.isBefore(inicioMes) && !dt.isAfter(fimMes);
                })
                .map(f -> f.getValorParcela() != null ? f.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorParcelasProximoMes = faturamentos.stream()
                .filter(f -> {
                    LocalDate dt = parseData(String.valueOf(f.getDataPagamento()));
                    return dt != null && !dt.isBefore(inicioProximoMes) && !dt.isAfter(fimProximoMes);
                })
                .map(f -> f.getValorParcela() != null ? f.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ==========================================
        // 2. RECEBIMENTOS & FATURAMENTO
        // ==========================================
        // Recebido Hoje (Verifica dataPagamentoReal ou dataPagamento da baixa)
        BigDecimal valorRecebidoHoje = faturamentos.stream()
                .filter(f -> Boolean.TRUE.equals(f.getPaga()))
                .filter(f -> {
                    if (f.getDataPagamentoReal() != null) {
                        return f.getDataPagamentoReal().isEqual(hoje);
                    }
                    LocalDate dt = parseData(f.getDataPagamento());
                    return dt != null && dt.isEqual(hoje);
                })
                .map(f -> f.getValorParcela() != null ? f.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Recebido na Semana
        BigDecimal valorRecebidoSemana = faturamentos.stream()
                .filter(f -> Boolean.TRUE.equals(f.getPaga()))
                .filter(f -> {
                    LocalDate dt = f.getDataPagamentoReal() != null ? f.getDataPagamentoReal() : parseData(f.getDataPagamento());
                    return dt != null && !dt.isBefore(inicioSemana) && !dt.isAfter(hoje);
                })
                .map(f -> f.getValorParcela() != null ? f.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Recebido no Mês
        BigDecimal valorRecebidoMes = faturamentos.stream()
                .filter(f -> Boolean.TRUE.equals(f.getPaga()))
                .filter(f -> {
                    LocalDate dt = f.getDataPagamentoReal() != null ? f.getDataPagamentoReal() : parseData(f.getDataPagamento());
                    return dt != null && !dt.isBefore(inicioMes) && !dt.isAfter(fimMes);
                })
                .map(f -> f.getValorParcela() != null ? f.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Faturadas (Emitidas) mas ainda Não Pagas
        BigDecimal valorParcelasFaturadas = faturamentos.stream()
                .filter(f -> Boolean.TRUE.equals(f.getFaturado()) && !Boolean.TRUE.equals(f.getPaga()))
                .map(f -> f.getValorParcela() != null ? f.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Parcelas Vencidas e Atrasadas (Não Pagas e vencimento anterior a hoje)
        BigDecimal valorParcelasAtrasadas = faturamentos.stream()
                .filter(f -> !Boolean.TRUE.equals(f.getPaga()))
                .filter(f -> {
                    LocalDate dt = parseData(f.getDataPagamento());
                    return dt != null && dt.isBefore(hoje);
                })
                .map(f -> f.getValorParcela() != null ? f.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ==========================================
        // 3. PAGAMENTOS EFETIVOS & PREVISTOS
        // ==========================================
        // Pagos Hoje (Despesas manuais + Compras aprovadas pagas)
        BigDecimal pagamentosHoje = pagamentos.stream()
                .filter(p -> p.getDataPagamento() != null && p.getDataPagamento().isEqual(hoje))
                .map(p -> p.getValorPagamentoEfetivo() != null ? p.getValorPagamentoEfetivo() : (p.getValor() != null ? p.getValor() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal comprasHoje = compras.stream()
                .filter(c -> Boolean.TRUE.equals(c.getPaga()) && c.getDataPagamento() != null && c.getDataPagamento().isEqual(hoje))
                .map(c -> c.getValorTotal() != null ? c.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorPagamentosHoje = pagamentosHoje.add(comprasHoje);

        // Pagos na Semana
        BigDecimal pagamentosSemana = pagamentos.stream()
                .filter(p -> p.getDataPagamento() != null && !p.getDataPagamento().isBefore(inicioSemana) && !p.getDataPagamento().isAfter(hoje))
                .map(p -> p.getValorPagamentoEfetivo() != null ? p.getValorPagamentoEfetivo() : (p.getValor() != null ? p.getValor() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal comprasSemana = compras.stream()
                .filter(c -> Boolean.TRUE.equals(c.getPaga()) && c.getDataPagamento() != null && !c.getDataPagamento().isBefore(inicioSemana) && !c.getDataPagamento().isAfter(hoje))
                .map(c -> c.getValorTotal() != null ? c.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorPagamentosSemana = pagamentosSemana.add(comprasSemana);

        // Pagos no Mês
        BigDecimal pagamentosMes = pagamentos.stream()
                .filter(p -> p.getDataPagamento() != null && !p.getDataPagamento().isBefore(inicioMes) && !p.getDataPagamento().isAfter(fimMes))
                .map(p -> p.getValorPagamentoEfetivo() != null ? p.getValorPagamentoEfetivo() : (p.getValor() != null ? p.getValor() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal comprasMes = compras.stream()
                .filter(c -> Boolean.TRUE.equals(c.getPaga()) && c.getDataPagamento() != null && !c.getDataPagamento().isBefore(inicioMes) && !c.getDataPagamento().isAfter(fimMes))
                .map(c -> c.getValorTotal() != null ? c.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorPagamentosMes = pagamentosMes.add(comprasMes);

        // Previsão de Pagamentos do Próximo Mês
        BigDecimal valorPagamentosProximoMes = pagamentos.stream()
                .filter(p -> p.getDataVencimento() != null && !p.getDataVencimento().isBefore(inicioProximoMes) && !p.getDataVencimento().isAfter(fimProximoMes))
                .map(p -> p.getValor() != null ? p.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Contas Atrasadas (Vencimento anterior a hoje e sem pagamento registrado)
        BigDecimal valorPagamentosAtrasadas = pagamentos.stream()
                .filter(p -> p.getDataPagamento() == null && p.getDataVencimento() != null && p.getDataVencimento().isBefore(hoje))
                .map(p -> p.getValor() != null ? p.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ==========================================
        // 4. FROTA
        // ==========================================
        int kmHoje = 0;
        int kmSemana = 0;
        int kmMes = 0;

        // ==========================================
        // 5. FOLHA & SALÁRIOS
        // ==========================================
        BigDecimal valorTotalSalarios = funcionarios.stream()
                .filter(f -> Boolean.TRUE.equals(f.getAtivo()))
                .map(f -> f.getSalario() != null ? f.getSalario() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalEncargos = valorTotalSalarios.multiply(new BigDecimal("0.28"));
        BigDecimal valorTotalBeneficios = valorTotalSalarios.multiply(new BigDecimal("0.08"));
        BigDecimal valorTotalComissao = valorContratosMes.multiply(new BigDecimal("0.05"));
        BigDecimal valorTotalFerias = valorTotalSalarios.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);

        BigDecimal valorTotalMes = valorTotalSalarios
                .add(valorTotalEncargos)
                .add(valorTotalBeneficios)
                .add(valorTotalComissao)
                .add(valorTotalFerias);

        return AdministracaoDTO.builder()
                // Vendas
                .valorContratosHoje(valorContratosHoje)
                .valorContratosSemana(valorContratosSemana)
                .valorContratosMes(valorContratosMes)
                .valorParcelasMes(valorParcelasMes)
                .valorParcelasProximoMes(valorParcelasProximoMes)
                // Recebimentos
                .valorRecebidoHoje(valorRecebidoHoje)
                .valorRecebidoSemana(valorRecebidoSemana)
                .valorRecebidoMes(valorRecebidoMes)
                .valorParcelasFaturadas(valorParcelasFaturadas)
                .valorParcelasAtrasadas(valorParcelasAtrasadas)
                // Pagamentos
                .valorPagamentosHoje(valorPagamentosHoje)
                .valorPagamentosSemana(valorPagamentosSemana)
                .valorPagamentosMes(valorPagamentosMes)
                .valorPagamentosProximoMes(valorPagamentosProximoMes)
                .valorPagamentosAtrasadas(valorPagamentosAtrasadas)
                // Frota
                .kilometrosPercorridosHoje(kmHoje)
                .kilometrosPercorridosSemana(kmSemana)
                .kilometrosPercorridosMes(kmMes)
                // Salários
                .valorTotalSalarios(valorTotalSalarios)
                .valorTotalComissao(valorTotalComissao)
                .valorTotalEncargos(valorTotalEncargos)
                .valorTotalFerias(valorTotalFerias)
                .valorTotalBeneficios(valorTotalBeneficios)
                .valorTotalMes(valorTotalMes)
                .valorTotalMesAnterior(valorTotalMes)
                .valorTotalPrevisaoProximoMes(valorTotalMes)
                .build();
    }

    private LocalDate parseData(Object dataObj) {
        if (dataObj == null) {
            return null;
        }
        if (dataObj instanceof LocalDate ld) {
            return ld;
        }
        String dataStr = dataObj.toString().trim();
        if (dataStr.isBlank() || dataStr.equalsIgnoreCase("null")) {
            return null;
        }
        try {
            if (dataStr.contains("/")) {
                return LocalDate.parse(dataStr, DATE_FORMATTER_BR);
            }
            return LocalDate.parse(dataStr);
        } catch (Exception e) {
            return null;
        }
    }
}