package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FeriasDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.FeriasMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Ferias;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.repository.escritorio.FeriasRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeriasService {

    private final FeriasRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final FeriasMapper mapper;

    private void validarRegrasFerias(FeriasDTO dto) {
        if (dto.getFuncionario().getId() == null) {
            throw new IllegalArgumentException("É obrigatório selecionar um funcionário válido.");
        }

        if (dto.getAnoReferenciaAquisitivo() == null) {
            throw new IllegalArgumentException("O ano de referência do exercício aquisitivo é obrigatório.");
        }

        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionario().getId())
                .orElseThrow(() -> new RuntimeException(STR."Funcionário não encontrado com o ID: \{dto.getFuncionario().getId()}"));

        if (funcionario.getAdmissao() == null) {
            throw new IllegalArgumentException("Não é possível calcular o direito às férias pois o funcionário não possui data de admissão cadastrada.");
        }

        // 1. Validação de Elegibilidade Temporal Inicial (Direito adquirido após 12 meses)
        long mesesTrabalhados = ChronoUnit.MONTHS.between(funcionario.getAdmissao(), dto.getDataInicio() != null ? dto.getDataInicio() : LocalDate.now());
        if (mesesTrabalhados < 12) {
            throw new IllegalArgumentException(STR."Solicitação Recusada! O funcionário possui apenas \{mesesTrabalhados} meses de empresa. É necessário ter no mínimo 12 meses de trabalho para ter direito a férias.");
        }

        int diasGozoSolicitados = 0;

        // Se NÃO for um pedido puramente de abono (venda total do saldo restante)
        if (Boolean.FALSE.equals(dto.getAbonoPecuniario()) || (dto.getDataInicio() != null && dto.getDataFim() != null)) {
            if (dto.getDataInicio() == null || dto.getDataFim() == null) {
                throw new IllegalArgumentException("As datas de início e fim são obrigatórias para períodos de gozo.");
            }
            if (dto.getDataFim().isBefore(dto.getDataInicio())) {
                throw new IllegalArgumentException("A data de término não pode ser anterior à data de início.");
            }
            diasGozoSolicitados = (int) ChronoUnit.DAYS.between(dto.getDataInicio(), dto.getDataFim()) + 1;

            // Regra da CLT: Nenhum período fracionado pode ser menor que 5 dias
            if (diasGozoSolicitados < 5) {
                throw new IllegalArgumentException("Pela legislação (CLT), nenhum período de férias fracionado pode ser menor do que 5 dias.");
            }
        }

        int diasAbonoSolicitados = dto.getQuantidadeDiasAbono() != null ? dto.getQuantidadeDiasAbono() : 0;

        // Regra da CLT: O abono pecuniário (venda) não pode ultrapassar 1/3 (10 dias)
        if (diasAbonoSolicitados > 10) {
            throw new IllegalArgumentException("Pela legislação trabalhista, o abono pecuniário é limitado ao máximo de 10 dias (1/3 do direito).");
        }

        // =================================================================================
        // CORREÇÃO DA TRAVA HISTÓRICA ACUMULADA: Soma o que o funcionário já gastou no ano
        // =================================================================================
        List<Ferias> historicoDoAno = repository.findByFuncionarioId(dto.getFuncionario().getId()).stream()
                .filter(f -> f.getAnoReferenciaAquisitivo().equals(dto.getAnoReferenciaAquisitivo()))
                .filter(f -> dto.getId() == null || !f.getId().equals(dto.getId())) // Ignora o próprio registro se for edição
                .filter(f -> f.getMotivoRecusa() == null || f.getMotivoRecusa().trim().isEmpty()) // Ignora os recusados pelo RH
                .toList();

        int totalDiasGozoConsumidos = historicoDoAno.stream().mapToInt(f -> f.getQuantidadeDias() != null ? f.getQuantidadeDias() : 0).sum();
        int totalDiasAbonoConsumidos = historicoDoAno.stream().mapToInt(f -> f.getQuantidadeDiasAbono() != null ? f.getQuantidadeDiasAbono() : 0).sum();

        int saldoFuturoGozo = totalDiasGozoConsumidos + diasGozoSolicitados;
        int saldoFuturoAbono = totalDiasAbonoConsumidos + diasAbonoSolicitados;

        if ((saldoFuturoGozo + saldoFuturoAbono) > 30) {
            int saldoRestanteDisponivel = 30 - (totalDiasGozoConsumidos + totalDiasAbonoConsumidos);
            throw new IllegalArgumentException(String.format(
                    "Estouro de Saldo! O funcionário já possui %d dias utilizados/agendados neste exercício de %d. Saldo disponível restante: %d dias.",
                    (totalDiasGozoConsumidos + totalDiasAbonoConsumidos),
                    dto.getAnoReferenciaAquisitivo(),
                    saldoRestanteDisponivel
            ));
        }

        // Seta as propriedades calculadas antes de persistir
        dto.setQuantidadeDias(diasGozoSolicitados);
        if (dto.getId() == null) {
            dto.setAprovada(false); // Todo pedido entra em triagem aguardando homologação do RH
        }
    }

    @Transactional
    public FeriasDTO salvar(FeriasDTO dto) {
        validarRegrasFerias(dto);
        Ferias entidade = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entidade));
    }

    public List<FeriasDTO> listarTodas() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<FeriasDTO> listarPorFuncionario(Long funcionarioId) {
        return repository.findByFuncionarioId(funcionarioId).stream()
                .map(mapper::toDto)
                .toList();
    }

    public FeriasDTO buscarPorId(Long id) {
        Ferias ferias = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(STR."Registro de férias não encontrado com o ID: " + id));
        return mapper.toDto(ferias);
    }

    @Transactional
    public FeriasDTO atualizar(Long id, FeriasDTO dto) {
        Ferias feriasExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(STR."Registro de férias não encontrado com o ID: " + id));

        if (dto.getMotivoRecusa() != null && !dto.getMotivoRecusa().trim().isEmpty()) {
            dto.setAprovada(false);
        } else {
            validarRegrasFerias(dto);
        }

        mapper.updateEntityFromDto(dto, feriasExistente);
        return mapper.toDto(repository.save(feriasExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException(STR."Não é possível deletar. Registro de férias não encontrado com o ID: \{id}");
        }
        repository.deleteById(id);
    }
}