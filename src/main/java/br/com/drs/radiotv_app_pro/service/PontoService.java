package br.com.drs.radiotv_app_pro.service;

import br.com.drs.radiotv_app_pro.model.Funcionario;
import br.com.drs.radiotv_app_pro.model.Ponto;
import br.com.drs.radiotv_app_pro.model.Usuario;
import br.com.drs.radiotv_app_pro.repository.FuncionarioRepository;
import br.com.drs.radiotv_app_pro.repository.PontoRepository;
import br.com.drs.radiotv_app_pro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PontoService {

    private final PontoRepository pontoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Ponto baterPontoSequencial(Long usuarioId) {
        // 1. Busca o usuário autenticado no banco
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário de acesso não encontrado."));

        // 2. Captura o ID numérico direto do seu modelo Usuario
        Long funcionarioId = usuario.getFuncionarioId();
        if (funcionarioId == null) {
            throw new IllegalStateException("Seu usuário não possui um funcionário CLT associado pelo administrador.");
        }

        // 3. Busca a entidade Funcionario usando o ID numérico obtido
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário vinculado não encontrado no sistema."));

        LocalDateTime agora = LocalDateTime.now();
        LocalDate hoje = agora.toLocalDate();

        // 4. Busca o ponto do dia usando o ID do funcionário
        Ponto ponto = pontoRepository.findByFuncionarioIdAndHoraEntradaBetween(
                funcionarioId, hoje.atStartOfDay(), hoje.atTime(23, 59, 59)
        ).stream().findFirst().orElse(null);

        if (ponto == null) {
            ponto = new Ponto();
            ponto.setFuncionario(funcionario);
            ponto.setHoraEntrada(agora);
            return pontoRepository.save(ponto);
        }

        if (Boolean.TRUE.equals(ponto.getEnviadoParaCorrecao())) {
            throw new IllegalStateException("Seu ponto de hoje está em triagem pelo RH. Regularize na tela de acertos.");
        }

        if (ponto.getHoraSaidaIntervalo() == null) {
            ponto.setHoraSaidaIntervalo(agora);
        } else if (ponto.getHoraEntradaIntervalo() == null) {
            ponto.setHoraEntradaIntervalo(agora);
        } else if (ponto.getHoraSaida() == null) {
            ponto.setHoraSaida(agora);
        } else if (ponto.getHoraEntradaExtra() == null) {
            ponto.setHoraEntradaExtra(agora);
        } else if (ponto.getHoraSaidaExtra() == null) {
            ponto.setHoraSaidaExtra(agora);
        } else {
            throw new IllegalArgumentException("Todos os limites de marcações para o dia de hoje já foram preenchidos!");
        }

        return salvarERecalcular(ponto);
    }

    @Transactional
    public Ponto corrigirPontoPeloFuncionario(Long id, Ponto dadosCorrigidos) {
        Ponto ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de ponto não encontrado."));

        if (!Boolean.TRUE.equals(ponto.getEnviadoParaCorrecao())) {
            throw new IllegalStateException("Este ponto não está liberado para correção.");
        }

        ponto.setHoraEntrada(dadosCorrigidos.getHoraEntrada());
        ponto.setHoraSaidaIntervalo(dadosCorrigidos.getHoraSaidaIntervalo());
        ponto.setHoraEntradaIntervalo(dadosCorrigidos.getHoraEntradaIntervalo());
        ponto.setHoraSaida(dadosCorrigidos.getHoraSaida());
        ponto.setHoraEntradaExtra(dadosCorrigidos.getHoraEntradaExtra());
        ponto.setHoraSaidaExtra(dadosCorrigidos.getHoraSaidaExtra());

        ponto.setPossuiInconsistencia(false);
        ponto.setEnviadoParaCorrecao(false);
        ponto.setDescricaoInconsistencia(null);

        return salvarERecalcular(ponto);
    }

    @Transactional
    public Ponto sinalizarInconsistencia(Long id, String motivoInconsistencia, String recadoParaFuncionario) {
        Ponto ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ponto não encontrado."));

        ponto.setPossuiInconsistencia(true);
        ponto.setDescricaoInconsistencia(motivoInconsistencia);
        ponto.setEnviadoParaCorrecao(true);
        ponto.setObservacaoEscritorio(recadoParaFuncionario);

        return pontoRepository.save(ponto);
    }

    public List<Ponto> listarPontosComErroParaOEscritorio() {
        return pontoRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getPossuiInconsistencia()))
                .toList();
    }

    public List<Ponto> listarMeusPontosParaCorrigir(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário de acesso não encontrado."));

        Long funcionarioId = usuario.getFuncionarioId();
        if (funcionarioId == null) {
            return List.of();
        }

        return pontoRepository.findAll().stream()
                .filter(p -> p.getFuncionario().getId().equals(funcionarioId) && Boolean.TRUE.equals(p.getEnviadoParaCorrecao()))
                .toList();
    }

    @Transactional
    public Ponto atualizarPontoPorEscritorio(Long id, Ponto novosDados) {
        Ponto ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ponto não encontrado."));

        ponto.setHoraEntrada(novosDados.getHoraEntrada());
        ponto.setHoraSaidaIntervalo(novosDados.getHoraSaidaIntervalo());
        ponto.setHoraEntradaIntervalo(novosDados.getHoraEntradaIntervalo());
        ponto.setHoraSaida(novosDados.getHoraSaida());
        ponto.setHoraEntradaExtra(novosDados.getHoraEntradaExtra());
        ponto.setHoraSaidaExtra(novosDados.getHoraSaidaExtra());

        ponto.setPossuiInconsistencia(false);
        ponto.setEnviadoParaCorrecao(false);

        return salvarERecalcular(ponto);
    }

    private Ponto salvarERecalcular(Ponto ponto) {
        ponto.setHoraExtraDia(calcularHorasExtrasDoDia(ponto));
        ponto = pontoRepository.save(ponto);

        ponto.setHoraExtraMes(calcularHorasExtrasDoMes(ponto.getFuncionario().getId()));
        return pontoRepository.save(ponto);
    }

    private LocalTime calcularHorasExtrasDoDia(Ponto ponto) {
        if (ponto.getHoraEntrada() == null || ponto.getHoraSaida() == null) {
            return LocalTime.MIDNIGHT;
        }
        Duration regular = Duration.between(ponto.getHoraEntrada(), ponto.getHoraSaida());
        if (ponto.getHoraSaidaIntervalo() != null && ponto.getHoraEntradaIntervalo() != null) {
            regular = regular.minus(Duration.between(ponto.getHoraSaidaIntervalo(), ponto.getHoraEntradaIntervalo()));
        }
        Duration extra = Duration.ZERO;
        if (ponto.getHoraEntradaExtra() != null && ponto.getHoraSaidaExtra() != null) {
            extra = Duration.between(ponto.getHoraEntradaExtra(), ponto.getHoraSaidaExtra());
        }
        Duration total = regular.plus(extra);
        Duration padrao = Duration.ofHours(8);

        if (total.compareTo(padrao) > 0) {
            Duration extras = total.minus(padrao);
            return LocalTime.of((int) extras.toHours(), (int) (extras.toMinutes() % 60));
        }
        return LocalTime.MIDNIGHT;
    }

    private String calcularHorasExtrasDoMes(Long funcionarioId) {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioMes = hoje.withDayOfMonth(1).atStartOfDay();
        LocalDateTime fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth()).atTime(23, 59, 59);

        List<Ponto> pontos = pontoRepository.findByFuncionarioIdAndHoraEntradaBetween(funcionarioId, inicioMes, fimMes);

        long totalMinutos = 0;
        for (Ponto p : pontos) {
            if (p != null && p.getHoraExtraDia() != null) {
                totalMinutos += (p.getHoraExtraDia().getHour() * 60) + p.getHoraExtraDia().getMinute();
            }
        }
        return String.format("%02d:%02d", totalMinutos / 60, totalMinutos % 60);
    }
}