package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FeriasDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.FeriasMapper;
import br.com.drs.radiotv_app_pro.model.enuns.Status;
import br.com.drs.radiotv_app_pro.model.escritorio.Ferias;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.model.escritorio.Pagamento;
import br.com.drs.radiotv_app_pro.repository.escritorio.FeriasRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeriasService {

    private final FeriasRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final PagamentoRepository pagamentoRepository;
    private final FeriasMapper mapper;

    private static final DateTimeFormatter FORMATTER_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private void validarRegrasFerias(FeriasDTO dto) {
        if (dto.getChaveUsuario() == null || dto.getChaveUsuario().isBlank()) {
            throw new IllegalArgumentException("A chave do funcionário é obrigatória.");
        }

        if (dto.getAnoReferenciaAquisitivo() == null) {
            throw new IllegalArgumentException("O ano de referência aquisitivo é obrigatório.");
        }

        Funcionario funcionario = (Funcionario) funcionarioRepository.findByChaveUsuario(dto.getChaveUsuario())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com a chave: " + dto.getChaveUsuario()));

        if (funcionario.getAdmissao() == null) {
            throw new IllegalArgumentException("O funcionário não possui data de admissão cadastrada.");
        }

        // Validação dos 12 meses de empresa
        LocalDate dataRef = dto.getDataInicio() != null ? dto.getDataInicio() : LocalDate.now();
        long mesesTrabalhados = ChronoUnit.MONTHS.between(funcionario.getAdmissao(), dataRef);
        if (mesesTrabalhados < 12) {
            throw new IllegalArgumentException("Solicitação Recusada! O funcionário possui apenas " + mesesTrabalhados + " meses de empresa. Mínimo exigido: 12 meses.");
        }

        int diasGozoSolicitados = 0;
        if (dto.getDataInicio() != null && dto.getDataFim() != null) {
            if (dto.getDataFim().isBefore(dto.getDataInicio())) {
                throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
            }
            diasGozoSolicitados = (int) ChronoUnit.DAYS.between(dto.getDataInicio(), dto.getDataFim()) + 1;
            if (diasGozoSolicitados < 5) {
                throw new IllegalArgumentException("Pela legislação (CLT), nenhum período de férias fracionado pode ser menor que 5 dias.");
            }
        }

        int diasAbonoSolicitados = dto.getQuantidadeDiasAbono() != null ? dto.getQuantidadeDiasAbono() : 0;
        if (diasAbonoSolicitados > 10) {
            throw new IllegalArgumentException("O abono pecuniário (venda) não pode ultrapassar 10 dias (1/3 do direito).");
        }

        // Histórico acumulado no ano
        List<Ferias> historicoDoAno = repository.findByChaveUsuario(dto.getChaveUsuario()).stream()
                .filter(f -> f.getAnoReferenciaAquisitivo().equals(dto.getAnoReferenciaAquisitivo()))
                .filter(f -> dto.getId() == null || !f.getId().equals(dto.getId()))
                .filter(f -> f.getMotivoRecusa() == null || f.getMotivoRecusa().trim().isEmpty())
                .toList();

        int totalGozoConsumido = historicoDoAno.stream().mapToInt(f -> f.getQuantidadeDias() != null ? f.getQuantidadeDias() : 0).sum();
        int totalAbonoConsumido = historicoDoAno.stream().mapToInt(f -> f.getQuantidadeDiasAbono() != null ? f.getQuantidadeDiasAbono() : 0).sum();

        if ((totalGozoConsumido + totalAbonoConsumido + diasGozoSolicitados + diasAbonoSolicitados) > 30) {
            int saldoRestante = 30 - (totalGozoConsumido + totalAbonoConsumido);
            throw new IllegalArgumentException(String.format("Estouro de Saldo! O funcionário já utilizou %d dias neste exercício. Saldo disponível: %d dias.",
                    (totalGozoConsumido + totalAbonoConsumido), saldoRestante));
        }

        dto.setQuantidadeDias(diasGozoSolicitados);
        if (dto.getId() == null) {
            dto.setAprovada(false); // Aguardando decisão do RH
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

    public List<FeriasDTO> listarPorChaveUsuario(String chaveUsuario) {
        return repository.findByChaveUsuario(chaveUsuario.trim()).stream()
                .map(mapper::toDto)
                .toList();
    }

    public FeriasDTO buscarPorId(Long id) {
        Ferias ferias = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de férias não encontrado com ID: " + id));
        return mapper.toDto(ferias);
    }

    @Transactional
    public FeriasDTO atualizar(Long id, FeriasDTO dto) {
        Ferias feriasExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de férias não encontrado com o ID: " + id));

        // Se for RECUSA com justificativa
        if (dto.getMotivoRecusa() != null && !dto.getMotivoRecusa().trim().isEmpty()) {
            feriasExistente.setAprovada(false);
            feriasExistente.setMotivoRecusa(dto.getMotivoRecusa().trim());
            return mapper.toDto(repository.save(feriasExistente));
        }

        // Se for APROVAÇÃO do RH
        if (Boolean.TRUE.equals(dto.getAprovada()) && !Boolean.TRUE.equals(feriasExistente.getAprovada())) {
            feriasExistente.setAprovada(true);
            feriasExistente.setMotivoRecusa(null);

            Ferias salva = repository.save(feriasExistente);

            // Lança o Contas a Pagar
            lancarPagamentoFerias(salva);

            return mapper.toDto(salva);
        } else {
            validarRegrasFerias(dto);
            mapper.updateEntityFromDto(dto, feriasExistente);
            return mapper.toDto(repository.save(feriasExistente));
        }
    }

    private void lancarPagamentoFerias(Ferias ferias) {
        Funcionario funcionario = (Funcionario) funcionarioRepository.findByChaveUsuario(ferias.getChaveUsuario())
                .orElse(null);

        if (funcionario == null) {
            log.warn("Funcionário não encontrado para chave: {}. Pagamento de férias não gerado.", ferias.getChaveUsuario());
            return;
        }

        BigDecimal salario = funcionario.getSalario() != null ? funcionario.getSalario() : BigDecimal.ZERO;
        BigDecimal valorDia = salario.divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP);

        int diasGozo = ferias.getQuantidadeDias() != null ? ferias.getQuantidadeDias() : 0;
        int diasAbono = ferias.getQuantidadeDiasAbono() != null ? ferias.getQuantidadeDiasAbono() : 0;

        // Cálculo dos dias de descanso + 1/3
        BigDecimal valorGozo = valorDia.multiply(new BigDecimal(diasGozo));
        BigDecimal tercoGozo = valorGozo.divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);

        // Cálculo dos dias vendidos (Abono) + 1/3
        BigDecimal valorAbono = valorDia.multiply(new BigDecimal(diasAbono));
        BigDecimal tercoAbono = valorAbono.divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);

        BigDecimal valorTotal = valorGozo.add(tercoGozo).add(valorAbono).add(tercoAbono);

        // CLT: Pagamento até 2 dias antes do início do descanso
        LocalDate dataVencimento = ferias.getDataInicio() != null
                ? ferias.getDataInicio().minusDays(2)
                : LocalDate.now();

        String descricao = String.format("Férias - %s (%d dias gozo + %d dias abono)",
                funcionario.getNome() != null ? funcionario.getNome() : ferias.getChaveUsuario(),
                diasGozo, diasAbono);

        Pagamento pag = Pagamento.builder()
                .descricao(descricao)
                .valor(valorTotal)
                .numeroDocumento("FERIAS-" + ferias.getId())
                .dataVencimento(dataVencimento) // Passa LocalDate direto (sem formatar para String)
                .status(Status.valueOf("PENDENTE"))
                .ativo(true)
                .build();

        pagamentoRepository.save(pag);
        log.info("Lançamento em Contas a Pagar gerado com sucesso para as Férias ID {}: R$ {} com vencimento em {}",
                ferias.getId(), valorTotal, dataVencimento);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Registro de férias não encontrado com o ID: " + id);
        }
        repository.deleteById(id);
    }
}