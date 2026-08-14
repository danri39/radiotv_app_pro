package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FolhaPagamentoDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.FolhaPagamentoMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Beneficios;
import br.com.drs.radiotv_app_pro.model.escritorio.FolhaPagamento;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.repository.escritorio.BeneficiosRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FolhaPagamentoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolhaPagamentoService {

    private final FolhaPagamentoRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final BeneficiosRepository beneficiosRepository;
    private final FolhaPagamentoMapper mapper;

    @Transactional
    public FolhaPagamentoDTO calcularFolha(FolhaPagamentoDTO dto) {
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new IllegalArgumentException(STR."Funcionário não localizado com ID: \{dto.getFuncionarioId()}"));

        if (funcionario.getSalario() == null || funcionario.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O funcionário selecionado não possui um salário base válido cadastrado no perfil.");
        }

        repository.findByFuncionarioIdAndMesAno(dto.getFuncionarioId(), dto.getMesAno()).ifPresent(f -> {
            throw new IllegalStateException(STR."A folha de pagamento para este funcionário referente ao período \{dto.getMesAno()} já foi gerada.");
        });

        BigDecimal bruto = funcionario.getSalario();

        BigDecimal inss = calcularInss(bruto);
        BigDecimal irrf = calcularIrrf(bruto.subtract(inss)); // Base de cálculo do IRRF é o Bruto menos o INSS

        List<Beneficios> beneficiosDoFuncionario = beneficiosRepository.findByFuncionarioId(funcionario.getId());
        BigDecimal descontosBeneficios = beneficiosDoFuncionario.stream()
                .map(b -> b.getValorFuncionario().add(b.getValorFamilia()).add(b.getValorDesconto()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescontos = inss.add(irrf).add(descontosBeneficios);
        BigDecimal liquido = bruto.subtract(totalDescontos);

        FolhaPagamento novaFolha = FolhaPagamento.builder()
                .funcionario(funcionario)
                .mesAno(dto.getMesAno())
                .salarioBruto(bruto)
                .descontoInss(inss)
                .descontoIrrf(irrf)
                .descontoBeneficios(descontosBeneficios)
                .totalDescontos(totalDescontos)
                .salarioLiquido(liquido)
                .fechada(true) // Entra gerada e fechada para o mês
                .build();

        return mapper.toDTO(repository.save(novaFolha));
    }

    public List<FolhaPagamentoDTO> listarTodas() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    public List<FolhaPagamentoDTO> listarPorFuncionario(Long funcionarioId) {
        return repository.findByFuncionarioId(funcionarioId).stream().map(mapper::toDTO).toList();
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Folha de pagamento não encontrada para exclusão.");
        }
        repository.deleteById(id);
    }

    private BigDecimal calcularInss(BigDecimal salario) {
        double sal = salario.doubleValue();
        double desconto = 0;

        if (sal <= 1518.00) {
            desconto = sal * 0.075;
        } else if (sal <= 2793.88) {
            desconto = (1518.00 * 0.075) + ((sal - 1518.00) * 0.09);
        } else if (sal <= 4190.83) {
            desconto = (1518.00 * 0.075) + ((2793.88 - 1518.00) * 0.09) + ((sal - 2793.88) * 0.12);
        } else if (sal <= 7786.02) {
            desconto = (1518.00 * 0.075) + ((2793.88 - 1518.00) * 0.09) + ((4190.83 - 2793.88) * 0.12) + ((sal - 4190.83) * 0.14);
        } else {
            desconto = 908.85; // Teto máximo de recolhimento do INSS
        }
        return BigDecimal.valueOf(desconto).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularIrrf(BigDecimal baseCalculo) {
        double base = baseCalculo.doubleValue();
        double imposto = 0;

        if (base <= 2259.20) {
            imposto = 0;
        } else if (base <= 2826.65) {
            imposto = (base * 0.075) - 169.44;
        } else if (base <= 3751.05) {
            imposto = (base * 0.15) - 381.44;
        } else if (base <= 4664.68) {
            imposto = (base * 0.225) - 662.77;
        } else {
            imposto = (base * 0.275) - 896.00;
        }

        if (imposto < 0) imposto = 0;
        return BigDecimal.valueOf(imposto).setScale(2, RoundingMode.HALF_UP);
    }
}