package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ContratoDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.ContratoMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Agencia;
import br.com.drs.radiotv_app_pro.model.escritorio.Cliente;
import br.com.drs.radiotv_app_pro.model.escritorio.Contrato;
import br.com.drs.radiotv_app_pro.model.escritorio.Vendedor;
import br.com.drs.radiotv_app_pro.repository.escritorio.AgenciaRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.ClienteRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.ContratoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.VendedorRepository;
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

        // Atualiza os campos básicos
        contratoExistente.setDataInicio(dto.getDataInicio());
        contratoExistente.setDataFinal(dto.getDataFinal());
        contratoExistente.setValorTotal(dto.getValorTotal());
        contratoExistente.setQuantidadeParcelas(dto.getQuantidadeParcelas());
        contratoExistente.setDataPrimeiroPagamento(dto.getDataPrimeiroPagamento());

        if (dto.getContratoBonificado() != null) {
            contratoExistente.setContratoBonificado(dto.getContratoBonificado());
        }
        if (dto.getAtivo() != null) {
            contratoExistente.setAtivo(dto.getAtivo());
        }

        // Atualiza o Cliente com segurança
        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
            contratoExistente.setCliente(cliente);
        }

        // Atualiza o Vendedor com segurança
        if (dto.getVendedorId() != null) {
            Vendedor vendedor = vendedorRepository.findById(dto.getVendedorId())
                    .orElseThrow(() -> new IllegalArgumentException("Vendedor não encontrado."));
            contratoExistente.setVendedor(vendedor);
        }

        // Atualiza a Agência com segurança (pode ser nula)
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