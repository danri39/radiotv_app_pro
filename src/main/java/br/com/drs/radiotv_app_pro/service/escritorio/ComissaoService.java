package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ComissaoDTO;
import br.com.drs.radiotv_app_pro.dto.escritorio.ResumoComissaoVendedorDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.ComissaoMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Comissao;
import br.com.drs.radiotv_app_pro.repository.escritorio.ComissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComissaoService {

    private final ComissaoRepository repository;
    private final ComissaoMapper mapper;

    @Transactional
    public ComissaoDTO salvar(ComissaoDTO dto) {
        Comissao entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    public List<ComissaoDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public ComissaoDTO buscarPorId(Long id) {
        Comissao comissao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comissão não encontrada com o ID: " + id));
        return mapper.toDto(comissao);
    }

    @Transactional(readOnly = true)
    public ResumoComissaoVendedorDTO obterResumoParaFolha(String chaveUsuario, LocalDate dataInicio, LocalDate dataFim) {
        List<Comissao> comissoes = repository.findByChaveUsuario(chaveUsuario).stream()
                .filter(c -> c.getDataPagamentoReal() != null)
                .filter(c -> !c.getDataPagamentoReal().isBefore(dataInicio) && !c.getDataPagamentoReal().isAfter(dataFim))
                .filter(c -> !Boolean.TRUE.equals(c.getPagaVendedor()))
                .toList();

        BigDecimal totalFaturas = comissoes.stream()
                .map(c -> c.getValorParcela() != null ? c.getValorParcela() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComissao = comissoes.stream()
                .map(c -> c.getValorComissaoVendedor() != null ? c.getValorComissaoVendedor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResumoComissaoVendedorDTO.builder()
                .chaveUsuario(chaveUsuario)
                .totalFaturasPagas(comissoes.size())
                .valorTotalFaturas(totalFaturas)
                .valorTotalComissao(totalComissao)
                .detalhes(comissoes.stream().map(mapper::toDto).toList())
                .build();
    }

    @Transactional
    public ComissaoDTO liquidarComissaoVendedor(Long id) {
        Comissao comissao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comissão não encontrada com o ID: " + id));

        comissao.setPagaVendedor(true);
        return mapper.toDto(repository.save(comissao));
    }

    @Transactional
    public ComissaoDTO liquidarComissaoAgencia(Long id, String numeroNota) {
        Comissao comissao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comissão não encontrada com o ID: " + id));

        comissao.setPagaAgencia(true);
        if (numeroNota != null && !numeroNota.isBlank()) {
            comissao.setNumeroNotaAgencia(numeroNota);
        }
        return mapper.toDto(repository.save(comissao));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Comissão não encontrada com o ID: " + id);
        }
        repository.deleteById(id);
    }
}