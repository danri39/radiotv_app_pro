package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.PontoDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.PontoMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Ponto;
import br.com.drs.radiotv_app_pro.repository.escritorio.PontoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PontoService {

    private final PontoRepository pontoRepository;
    private final PontoMapper pontoMapper;

    @Transactional
    public PontoDTO registrarPontoSimples(String chaveUsuario, String tipo) {
        if (chaveUsuario == null || chaveUsuario.isBlank()) {
            throw new IllegalArgumentException("A chave do usuário é obrigatória para registrar o ponto.");
        }

        LocalDateTime agoraServidor = LocalDateTime.now();
        LocalDateTime inicioDoDia = agoraServidor.toLocalDate().atStartOfDay();
        LocalDateTime fimDoDia = agoraServidor.toLocalDate().atTime(LocalTime.MAX);

        // Busca o ponto já iniciado no dia de hoje para essa chaveUsuario ou cria uma nova entidade
        Ponto ponto = pontoRepository.findFirstByChaveUsuarioAndHoraEntradaBetween(chaveUsuario, inicioDoDia, fimDoDia)
                .orElseGet(() -> Ponto.builder()
                        .chaveUsuario(chaveUsuario)
                        .possuiInconsistencia(false)
                        .enviadoParaCorrecao(false)
                        .build());

        switch (tipo.toUpperCase().trim()) {
            case "ENTRADA" -> ponto.setHoraEntrada(agoraServidor);
            case "SAIDA_INTERVALO" -> ponto.setHoraSaidaIntervalo(agoraServidor);
            case "ENTRADA_INTERVALO" -> ponto.setHoraEntradaIntervalo(agoraServidor);
            case "SAIDA" -> ponto.setHoraSaida(agoraServidor);
            case "ENTRADA_EXTRA" -> ponto.setHoraEntradaExtra(agoraServidor);
            case "SAIDA_EXTRA" -> ponto.setHoraSaidaExtra(agoraServidor);
            default -> throw new IllegalArgumentException("Tipo de batida de ponto inválido: " + tipo);
        }

        // Se a primeira batida do dia não foi a ENTRADA, define a primeira marcação como horaEntrada
        if (ponto.getHoraEntrada() == null) {
            ponto.setHoraEntrada(agoraServidor);
        }

        verificarInconsistencias(ponto);
        Ponto salvo = pontoRepository.save(ponto);
        return pontoMapper.toDTO(salvo);
    }

    public List<PontoDTO> buscarInconsistenciasParaEscritorio() {
        LocalDate hoje = LocalDate.now();
        return pontoRepository.findAll().stream()
                .filter(p -> {
                    // Considera dias anteriores ao dia de hoje com marcações incompletas
                    boolean ehAnterior = p.getHoraEntrada() != null && p.getHoraEntrada().toLocalDate().isBefore(hoje);
                    return ehAnterior && Boolean.TRUE.equals(p.getPossuiInconsistencia());
                })
                .map(pontoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<PontoDTO> buscarMeusAcertosPendentes(String chaveUsuario) {
        return pontoRepository.findByChaveUsuarioAndEnviadoParaCorrecaoTrue(chaveUsuario).stream()
                .map(pontoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PontoDTO solicitarCorrecaoRH(Long id, String observacao) {
        Ponto ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de ponto não encontrado com ID: " + id));

        ponto.setEnviadoParaCorrecao(true);
        ponto.setObservacaoEscritorio(observacao);
        Ponto salvo = pontoRepository.save(ponto);
        return pontoMapper.toDTO(salvo);
    }

    @Transactional
    public PontoDTO realizarCorrecaoFuncionario(Long id, PontoDTO dto) {
        Ponto ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de ponto não encontrado com ID: " + id));

        // SEGURANÇA: Só aceita preencher se o campo no banco estiver VAZIO (NULL)
        if (ponto.getHoraEntrada() == null && dto.getHoraEntrada() != null) {
            ponto.setHoraEntrada(dto.getHoraEntrada());
        }
        if (ponto.getHoraSaidaIntervalo() == null && dto.getHoraSaidaIntervalo() != null) {
            ponto.setHoraSaidaIntervalo(dto.getHoraSaidaIntervalo());
        }
        if (ponto.getHoraEntradaIntervalo() == null && dto.getHoraEntradaIntervalo() != null) {
            ponto.setHoraEntradaIntervalo(dto.getHoraEntradaIntervalo());
        }
        if (ponto.getHoraSaida() == null && dto.getHoraSaida() != null) {
            ponto.setHoraSaida(dto.getHoraSaida());
        }

        verificarInconsistencias(ponto);

        // Se corrigiu as batidas faltantes, normaliza o ponto e remove da pendência
        if (!Boolean.TRUE.equals(ponto.getPossuiInconsistencia())) {
            ponto.setEnviadoParaCorrecao(false);
            ponto.setObservacaoEscritorio(null);
        }

        Ponto salvo = pontoRepository.save(ponto);
        return pontoMapper.toDTO(salvo);
    }

    private void verificarInconsistencias(Ponto ponto) {
        int totalBatidas = contarBatidas(ponto);

        if (totalBatidas < 4) {
            ponto.setPossuiInconsistencia(true);
            StringBuilder desc = new StringBuilder("Registro incompleto: ");
            if (ponto.getHoraEntrada() == null) desc.append("[Falta Entrada] ");
            if (ponto.getHoraSaidaIntervalo() == null) desc.append("[Falta Saída Intervalo] ");
            if (ponto.getHoraEntradaIntervalo() == null) desc.append("[Falta Retorno Intervalo] ");
            if (ponto.getHoraSaida() == null) desc.append("[Falta Saída] ");
            ponto.setDescricaoInconsistencia(desc.toString().trim());
        } else {
            ponto.setPossuiInconsistencia(false);
            ponto.setDescricaoInconsistencia(null);
        }
    }

    private int contarBatidas(Ponto ponto) {
        int count = 0;
        if (ponto.getHoraEntrada() != null) count++;
        if (ponto.getHoraSaidaIntervalo() != null) count++;
        if (ponto.getHoraEntradaIntervalo() != null) count++;
        if (ponto.getHoraSaida() != null) count++;
        return count;
    }

    @Transactional
    public PontoDTO salvar(PontoDTO pontoDTO) {
        Ponto ponto = pontoMapper.toEntity(pontoDTO);
        verificarInconsistencias(ponto);
        Ponto salvo = pontoRepository.save(ponto);
        return pontoMapper.toDTO(salvo);
    }

    public List<PontoDTO> buscarTodos() {
        return pontoRepository.findAll().stream().map(pontoMapper::toDTO).toList();
    }

    public Optional<PontoDTO> buscarPorId(Long id) {
        return pontoRepository.findById(id).map(pontoMapper::toDTO);
    }

    @Transactional
    public PontoDTO atualizar(Long id, PontoDTO pontoDTO) {
        return pontoRepository.findById(id)
                .map(pontoExistente -> {
                    pontoMapper.updateEntityFromDto(pontoDTO, pontoExistente);
                    verificarInconsistencias(pontoExistente);
                    Ponto atualizado = pontoRepository.save(pontoExistente);
                    return pontoMapper.toDTO(atualizado);
                })
                .orElseThrow(() -> new RuntimeException("Registro de ponto não encontrado com ID: " + id));
    }

    @Transactional
    public void deletar(Long id) {
        if (!pontoRepository.existsById(id)) {
            throw new RuntimeException("Registro de ponto não encontrado com ID: " + id);
        }
        pontoRepository.deleteById(id);
    }
}