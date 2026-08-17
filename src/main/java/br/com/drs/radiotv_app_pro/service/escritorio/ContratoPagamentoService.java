package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ContratoPagamentoDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.ContratoPagamentoMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.ContratoPagamento;
import br.com.drs.radiotv_app_pro.model.escritorio.Recebimento;
import br.com.drs.radiotv_app_pro.repository.escritorio.ContratoPagamentoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.ContratoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.RecebimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContratoPagamentoService {

    private final ContratoPagamentoRepository repository;
    private final ContratoPagamentoMapper mapper;
    private final ContratoRepository contratoRepository;
    private final RecebimentoRepository recebimentoRepository;

    @Transactional
    public ContratoPagamentoDTO salvar(ContratoPagamentoDTO dto) {
        ContratoPagamento entidade = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entidade));
    }

    public List<ContratoPagamentoDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ContratoPagamentoDTO buscarPorId(Long id) {
        ContratoPagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
        return mapper.toDTO(pagamento);
    }

    @Transactional
    public ContratoPagamentoDTO atualizar(Long id, ContratoPagamentoDTO dto) {
        ContratoPagamento pagamentoExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        mapper.updateEntityFromDto(dto, pagamentoExistente);

        return mapper.toDTO(repository.save(pagamentoExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Pagamento não encontrado");
        }
        repository.deleteById(id);
    }

    @Transactional
    public ContratoPagamentoDTO faturarParcela(Long id) {
        ContratoPagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        if (Boolean.TRUE.equals(pagamento.getFaturado())) {
            throw new IllegalStateException("Esta parcela já foi faturada.");
        }

        // Gera o código da fatura
        String codigoFatura = "FAT-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        pagamento.setFaturado(true);
        pagamento.setNumeroFatura(codigoFatura);

        // Salva e força a persistência imediata no banco antes de passar pelo Mapper
        ContratoPagamento salvo = repository.saveAndFlush(pagamento);
        return mapper.toDTO(salvo);
    }

    @Transactional
    public ContratoPagamentoDTO baixarParcela(Long id) {
        ContratoPagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela/Pagamento não encontrado"));

        pagamento.setPaga(true);
        pagamento.setDataPagamentoReal(LocalDate.now());

        ContratoPagamento salvo = repository.save(pagamento);

        try {
            Recebimento recebimento = Recebimento.builder()
                    .contrato(salvo.getContrato())
                    .numeroFatura(salvo.getNumeroFatura())
                    .dataVencimento(salvo.getDataPagamento())
                    .dataPagamento(LocalDate.now())
                    .valorParcela(salvo.getValorParcela())
                    .valorEfetivoPago(salvo.getValorParcela())
                    .pagoComissao(true)
                    .build();

            recebimentoRepository.save(recebimento);
        } catch (Exception e) {
            // Garante que se a tabela recebimento tiver outra estrutura, não quebre a baixa principal
            System.err.println("Aviso ao salvar recebimento: " + e.getMessage());
        }

        return mapper.toDTO(salvo);
    }

    @Transactional
    public ContratoPagamentoDTO estornarParcela(Long id) {
        ContratoPagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela/Pagamento não encontrado"));

        pagamento.setPaga(false);
        pagamento.setDataPagamentoReal(null);

        ContratoPagamento salvo = repository.save(pagamento);
        return mapper.toDTO(salvo);
    }
}