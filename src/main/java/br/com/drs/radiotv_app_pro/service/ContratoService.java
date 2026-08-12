package br.com.drs.radiotv_app_pro.service;

import br.com.drs.radiotv_app_pro.dto.ContratoDTO;
import br.com.drs.radiotv_app_pro.mapper.ContratoMapper;
import br.com.drs.radiotv_app_pro.model.Agencia;
import br.com.drs.radiotv_app_pro.model.Cliente;
import br.com.drs.radiotv_app_pro.model.Contrato;
import br.com.drs.radiotv_app_pro.model.Vendedor;
import br.com.drs.radiotv_app_pro.repository.AgenciaRepository;
import br.com.drs.radiotv_app_pro.repository.ClienteRepository;
import br.com.drs.radiotv_app_pro.repository.ContratoRepository;
import br.com.drs.radiotv_app_pro.repository.VendedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository repository;
    private final ContratoMapper mapper;
    private final ClienteRepository clienteRepository;
    private final AgenciaRepository agenciaRepository;
    private final VendedorRepository vendedorRepository;

    @Transactional
    public ContratoDTO salvar(ContratoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        Vendedor vendedor = vendedorRepository.findById(dto.getVendedorId())
                .orElseThrow(() -> new IllegalArgumentException("Vendedor não encontrado."));

        Contrato contrato = mapper.toEntity(dto);
        contrato.setCliente(cliente);
        contrato.setVendedor(vendedor);

        if (dto.getAgenciaId() != null) {
            Agencia agencia = agenciaRepository.findById(dto.getAgenciaId())
                    .orElseThrow(() -> new IllegalArgumentException("Agência não cadastrada no sistema."));
            contrato.setAgencia(agencia);
        } else {
            contrato.setAgencia(null);
        }

        vincularFilhos(contrato);
        return mapper.toDTO(repository.save(contrato));
    }

    public List<ContratoDTO> listarTodos() {
        // Usa a query otimizada do repositório para popular cliente, vendedor e agência
        return repository.findAllComRelacionamentos().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ContratoDTO buscarPorId(Long id) {
        Contrato contrato = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));
        return mapper.toDTO(contrato);
    }

    @Transactional
    public ContratoDTO atualizar(Long id, ContratoDTO dto) {
        Contrato contratoExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        mapper.updateEntityFromDto(dto, contratoExistente);

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        contratoExistente.setCliente(cliente);

        Vendedor vendedor = vendedorRepository.findById(dto.getVendedorId())
                .orElseThrow(() -> new IllegalArgumentException("Vendedor não encontrado."));
        contratoExistente.setVendedor(vendedor);

        if (dto.getAgenciaId() != null) {
            Agencia agencia = agenciaRepository.findById(dto.getAgenciaId())
                    .orElseThrow(() -> new IllegalArgumentException("Agência não cadastrada no sistema."));
            contratoExistente.setAgencia(agencia);
        } else {
            contratoExistente.setAgencia(null);
        }

        vincularFilhos(contratoExistente);
        return mapper.toDTO(repository.save(contratoExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Contrato não encontrado");
        }
        repository.deleteById(id);
    }

    private void vincularFilhos(Contrato contrato) {
        if (contrato.getMidias() != null) {
            contrato.getMidias().forEach(midia -> midia.setContrato(contrato));
        }
        if (contrato.getPagamentos() != null) {
            contrato.getPagamentos().forEach(pagamento -> pagamento.setContrato(contrato));
        }
    }
}