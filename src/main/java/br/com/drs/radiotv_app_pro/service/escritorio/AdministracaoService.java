package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.AdministracaoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Contrato;
import br.com.drs.radiotv_app_pro.model.escritorio.Faturamento;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.repository.escritorio.ContratoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FaturamentoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministracaoService {

    private final ContratoRepository contratoRepository;
    private final FaturamentoRepository faturamentoRepository;
    private final FuncionarioRepository funcionarioRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        BigDecimal valorRecebidoHoje = faturamentos.stream()
                .filter(f -> Boolean.TRUE.equals(f.getPaga()))
                .filter(f -> {
                    LocalDate dt = parseData(String.valueOf(f.getDataPagamento()));
                    return dt != null && dt.isEqual(hoje);
                })
                .map(f -> f.getValorParcela() != null ? f.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorRecebidoSemana = faturamentos.stream()
                .filter(f -> Boolean.TRUE.equals(f.getPaga()))
                .filter(f -> {
                    LocalDate dt = parseData(String.valueOf(f.getDataPagamento()));
                    return dt != null && !dt.isBefore(inicioSemana) && !dt.isAfter(hoje);
                })
                .map(f -> f.getValorParcela() != null ? f.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorRecebidoMes = faturamentos.stream()
                .filter(f -> Boolean.TRUE.equals(f.getPaga()))
                .filter(f -> {
                    LocalDate dt = parseData(String.valueOf(f.getDataPagamento()));
                    return dt != null && !dt.isBefore(inicioMes) && !dt.isAfter(fimMes);
                })
                .map(f -> f.getValorParcela() != null ? f.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorParcelasFaturadas = faturamentos.stream()
                .filter(f -> Boolean.TRUE.equals(f.getFaturado()) && !Boolean.TRUE.equals(f.getPaga()))
                .map(f -> f.getValorParcela() != null ? f.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorParcelasAtrasadas = faturamentos.stream()
                .filter(f -> !Boolean.TRUE.equals(f.getPaga()))
                .filter(f -> {
                    LocalDate dt = parseData(String.valueOf(f.getDataPagamento()));
                    return dt != null && dt.isBefore(hoje);
                })
                .map(f -> f.getValorParcela() != null ? f.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ==========================================
        // 3. PAGAMENTOS (CONTAS A PAGAR)
        // ==========================================
        BigDecimal valorPagamentosHoje = BigDecimal.ZERO;
        BigDecimal valorPagamentosSemana = BigDecimal.ZERO;
        BigDecimal valorPagamentosMes = BigDecimal.ZERO;
        BigDecimal valorPagamentosProximoMes = BigDecimal.ZERO;
        BigDecimal valorPagamentosAtrasadas = BigDecimal.ZERO;

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
        BigDecimal valorTotalFerias = valorTotalSalarios.divide(new BigDecimal("12"), 2, java.math.RoundingMode.HALF_UP);

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

    private LocalDate parseData(String dataStr) {
        if (dataStr == null || dataStr.isBlank()) {
            return null;
        }
        try {
            if (dataStr.contains("/")) {
                return LocalDate.parse(dataStr, DATE_FORMATTER);
            }
            return LocalDate.parse(dataStr);
        } catch (Exception e) {
            return null;
        }
    }
}