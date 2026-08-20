package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FuncionarioBeneficioDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Beneficios;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.model.escritorio.FuncionarioBeneficio;
import br.com.drs.radiotv_app_pro.repository.escritorio.BeneficiosRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioBeneficioRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuncionarioBeneficioService {

    private final FuncionarioBeneficioRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final BeneficiosRepository beneficiosRepository;

    @Transactional
    public FuncionarioBeneficioDTO salvar(FuncionarioBeneficioDTO dto) {
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com ID: " + dto.getFuncionarioId()));

        Beneficios beneficio = beneficiosRepository.findById(dto.getBeneficioId())
                .orElseThrow(() -> new IllegalArgumentException("Benefício não encontrado no catálogo com ID: " + dto.getBeneficioId()));

        int dependentes = dto.getQuantidadeDependentes() != null ? dto.getQuantidadeDependentes() : 0;
        BigDecimal valorTitular = BigDecimal.ZERO;
        BigDecimal valorDependentes = BigDecimal.ZERO;

        // Regras de cálculo automático pelo tipo de modalidade
        if (beneficio.getModalidadeCobranca() != null) {
            switch (beneficio.getModalidadeCobranca()) {
                case VALOR_FIXO -> {
                    valorTitular = beneficio.getValorFuncionario() != null ? beneficio.getValorFuncionario() : BigDecimal.ZERO;
                    BigDecimal custoDep = beneficio.getValorFamilia() != null ? beneficio.getValorFamilia() : BigDecimal.ZERO;
                    valorDependentes = custoDep.multiply(BigDecimal.valueOf(dependentes));
                }
                case PERCENTUAL_SALARIO -> {
                    BigDecimal salario = funcionario.getSalario() != null ? funcionario.getSalario() : BigDecimal.ZERO;
                    BigDecimal percentual = beneficio.getPercentualCoparticipacao() != null ? beneficio.getPercentualCoparticipacao() : BigDecimal.valueOf(6.0);
                    valorTitular = salario.multiply(percentual).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                }
                case COPARTICIPACAO -> {
                    valorTitular = beneficio.getValorFuncionario() != null ? beneficio.getValorFuncionario() : BigDecimal.ZERO;
                }
                case CONSUMO_VARIAVEL -> {
                    // Valores variáveis são lançados pontualmente na folha
                    valorTitular = BigDecimal.ZERO;
                }
            }
        }

        BigDecimal total = valorTitular.add(valorDependentes);

        FuncionarioBeneficio fb = FuncionarioBeneficio.builder()
                .funcionario(funcionario)
                .beneficio(beneficio)
                .quantidadeDependentes(dependentes)
                .valorTitularCalculado(valorTitular)
                .valorDependentesCalculado(valorDependentes)
                .valorTotalDesconto(total)
                .dataAdesao(dto.getDataAdesao() != null ? dto.getDataAdesao() : LocalDate.now())
                .ativo(dto.getAtivo() != null ? dto.getAtivo() : true)
                .observacao(dto.getObservacao())
                .build();

        return toDTO(repository.save(fb));
    }

    public List<FuncionarioBeneficioDTO> listarTodos() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public List<FuncionarioBeneficioDTO> listarPorFuncionario(Long funcionarioId) {
        return repository.findByFuncionarioId(funcionarioId).stream().map(this::toDTO).toList();
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Vínculo não encontrado.");
        }
        repository.deleteById(id);
    }

    private FuncionarioBeneficioDTO toDTO(FuncionarioBeneficio fb) {
        return FuncionarioBeneficioDTO.builder()
                .id(fb.getId())
                .funcionarioId(fb.getFuncionario().getId())
                .nomeFuncionario(fb.getFuncionario().getNome())
                .beneficioId(fb.getBeneficio().getId())
                .nomeBeneficio(fb.getBeneficio().getNomeBeneficio())
                .modalidadeCobranca(fb.getBeneficio().getModalidadeCobranca() != null ? fb.getBeneficio().getModalidadeCobranca().name() : null)
                .quantidadeDependentes(fb.getQuantidadeDependentes())
                .valorTitularCalculado(fb.getValorTitularCalculado())
                .valorDependentesCalculado(fb.getValorDependentesCalculado())
                .valorTotalDesconto(fb.getValorTotalDesconto())
                .dataAdesao(fb.getDataAdesao())
                .ativo(fb.getAtivo())
                .observacao(fb.getObservacao())
                .build();
    }
}