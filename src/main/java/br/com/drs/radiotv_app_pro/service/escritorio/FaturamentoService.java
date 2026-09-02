package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FaturamentoDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.FaturamentoMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Comissao;
import br.com.drs.radiotv_app_pro.model.escritorio.Contrato;
import br.com.drs.radiotv_app_pro.model.escritorio.Faturamento;
import br.com.drs.radiotv_app_pro.repository.escritorio.ComissaoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.ContratoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FaturamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaturamentoService {

    private final FaturamentoRepository repository;
    private final FaturamentoMapper mapper;
    private final ComissaoRepository comissaoRepository;
    private final ContratoRepository contratoRepository;

    @Transactional
    public FaturamentoDTO salvar(FaturamentoDTO dto) {
        Faturamento entidade = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entidade));
    }

    public List<FaturamentoDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public FaturamentoDTO buscarPorId(Long id) {
        Faturamento fatura = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faturamento não encontrado ID: " + id));
        return mapper.toDTO(fatura);
    }

    @Transactional
    public FaturamentoDTO atualizar(Long id, FaturamentoDTO dto) {
        Faturamento faturaExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faturamento não encontrado ID: " + id));

        boolean jaEstavaPaga = Boolean.TRUE.equals(faturaExistente.getPaga());
        mapper.updateEntityFromDto(dto, faturaExistente);
        faturaExistente.setId(id);

        if (!jaEstavaPaga && Boolean.TRUE.equals(faturaExistente.getPaga())) {
            LocalDate hoje = LocalDate.now();
            faturaExistente.setDataPagamentoReal(hoje);
            faturaExistente.setDataPagamento(hoje);

            Faturamento salva = repository.save(faturaExistente);
            gerarRegistroComissao(salva);
            return mapper.toDTO(salva);
        }

        return mapper.toDTO(repository.save(faturaExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Faturamento não encontrado ID: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public FaturamentoDTO faturarParcela(Long id) {
        Faturamento fatura = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fatura não encontrada ID: " + id));

        fatura.setFaturado(true);
        if (fatura.getNumeroFatura() == null || fatura.getNumeroFatura().isBlank()) {
            fatura.setNumeroFatura("FAT-" + fatura.getId() + "/" + LocalDate.now().getYear());
        }

        return mapper.toDTO(repository.save(fatura));
    }

    @Transactional
    public FaturamentoDTO baixarParcela(Long id) {
        Faturamento fatura = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fatura não encontrada ID: " + id));

        LocalDate hoje = LocalDate.now();
        fatura.setPaga(true);
        fatura.setDataPagamentoReal(hoje);
        fatura.setDataPagamento(hoje);

        Faturamento faturaSalva = repository.save(fatura);

        gerarRegistroComissao(faturaSalva);

        return mapper.toDTO(faturaSalva);
    }

    @Transactional
    public FaturamentoDTO estornarParcela(Long id) {
        Faturamento fatura = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fatura não encontrada ID: " + id));

        fatura.setPaga(false);
        fatura.setDataPagamentoReal(null);
        fatura = repository.save(fatura);

        if (fatura.getNumeroFatura() != null) {
            String numFatura = fatura.getNumeroFatura();
            List<Comissao> comissoes = comissaoRepository.findAll().stream()
                    .filter(c -> numFatura.equals(c.getNumeroFatura()) && !Boolean.TRUE.equals(c.getPagaVendedor()))
                    .toList();
            comissaoRepository.deleteAll(comissoes);
        }

        return mapper.toDTO(fatura);
    }

    private void gerarRegistroComissao(Faturamento fatura) {
        Contrato contrato = fatura.getContrato();
        if (contrato == null && fatura.getContrato() != null && fatura.getContrato().getId() != null) {
            contrato = contratoRepository.findById(fatura.getContrato().getId()).orElse(null);
        }

        if (contrato == null) {
            log.warn("Nenhum contrato associado à fatura ID: {}. Comissão não gerada.", fatura.getId());
            return;
        }

        String chaveVendedor = contrato.getChaveUsuario();
        if (chaveVendedor == null || chaveVendedor.isBlank()) {
            log.warn("Contrato ID: {} não possui chaveUsuario preenchida. Comissão não gerada.", contrato.getId());
            return;
        }

        BigDecimal valorParcela = fatura.getValorParcela() != null ? fatura.getValorParcela() : BigDecimal.ZERO;

        // Percentuais padrões de comissão (10% vendedor e 5% agência se houver)
        BigDecimal comissaoVendedor = valorParcela.multiply(new BigDecimal("0.10"));
        BigDecimal comissaoAgencia = (contrato.getAgencia() != null)
                ? valorParcela.multiply(new BigDecimal("0.05"))
                : BigDecimal.ZERO;

        String numeroFatura = (fatura.getNumeroFatura() != null && !fatura.getNumeroFatura().isBlank())
                ? fatura.getNumeroFatura()
                : ("FAT-" + fatura.getId());

        Comissao novaComissao = Comissao.builder()
                .contrato(contrato)
                .agencia(contrato.getAgencia())
                .chaveUsuario(chaveVendedor)
                .dataPagamentoReal(LocalDate.now())
                .valorParcela(valorParcela)
                .numeroFatura(numeroFatura)
                .valorComissaoVendedor(comissaoVendedor)
                .valorComissaoAgencia(comissaoAgencia)
                .pagaVendedor(false)
                .pagaAgencia(false)
                .ativo(true)
                .build();

        comissaoRepository.save(novaComissao);
        log.info("Comissão gerada com sucesso! Vendedor: {}, Contrato: {}, Fatura: {}, Valor: R$ {}",
                chaveVendedor, contrato.getId(), numeroFatura, comissaoVendedor);
    }
}