package br.com.drs.radiotv_app_pro.service;

import br.com.drs.radiotv_app_pro.dto.AgenciaDTO;
import br.com.drs.radiotv_app_pro.mapper.AgenciaMapper;
import br.com.drs.radiotv_app_pro.model.Agencia;
import br.com.drs.radiotv_app_pro.model.enuns.TipoPessoa;
import br.com.drs.radiotv_app_pro.repository.AgenciaRepository;
import br.com.drs.radiotv_app_pro.util.ValidaDocumentoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgenciaService {

    private final AgenciaRepository agenciaRepository;

    private final AgenciaMapper agenciaMapper;

    private final ViaCepService viaCepService;

    @Transactional(readOnly = true)
    public List<AgenciaDTO> listarTodas() {
        return agenciaRepository.findAll().stream()
                .map(agenciaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AgenciaDTO buscarPorId(Long id) {
        Agencia agencia = agenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agência não encontrada com o ID: " + id));
        return agenciaMapper.toDTO(agencia);
    }

    @Transactional
    public AgenciaDTO salvar(AgenciaDTO dto) {
        if (agenciaRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado para outra agência.");
        }
        Agencia agencia = agenciaMapper.toEntity(dto);
        if (agencia.getCep() != null && !agencia.getCep().isBlank()) {
            if (agencia.getLogradouro() == null || agencia.getLogradouro().isBlank()) {
                ViaCepService.ViaCepDTO dadosCep = viaCepService.buscarEnderecoPorCep(agencia.getCep());
                if (dadosCep != null) {
                    agencia.setLogradouro(dadosCep.getLogradouro());
                    agencia.setBairro(dadosCep.getBairro());
                    agencia.setCidade(dadosCep.getLocalidade());
                    agencia.setEstado(dadosCep.getUf());
                }
            }
        }

        if (agencia.getTipoPessoa() == TipoPessoa.FISICA) {
            if (!ValidaDocumentoUtil.isCPF(dto.getCpf())) {
                throw new IllegalArgumentException("CPF invalido favor acertar.");
            }
            if (agenciaRepository.existsByCpf(agencia.getCpf())) {
                throw new IllegalArgumentException("CPF já cadastrado.");
            }
            agencia.setCnpj(null);
            agencia.setInscricao(null);
        } else if (agencia.getTipoPessoa() == TipoPessoa.JURIDICA) {
            if (!ValidaDocumentoUtil.isCNPJ(dto.getCnpj())) {
                throw new IllegalArgumentException("CNPJ invalido favor acertar.");
            }
            if (agenciaRepository.existsByCnpj(agencia.getCnpj())) {
                throw new IllegalArgumentException("CNPJ já cadastrado.");
            }
            agencia.setCpf(null);
            agencia.setRg(null);
        }

        agencia.setAtivo(true);
        return agenciaMapper.toDTO(agenciaRepository.save(agencia));
    }

    @Transactional
    public AgenciaDTO atualizar(Long id, AgenciaDTO dto) {
        Agencia agenciaExistente = agenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agência não encontrada para atualização."));

        if (dto.getCep() != null && !dto.getCep().equals(agenciaExistente.getCep())) {
            ViaCepService.ViaCepDTO dadosCep = viaCepService.buscarEnderecoPorCep(dto.getCep());
            if (dadosCep != null) {
                agenciaExistente.setLogradouro(dadosCep.getLogradouro());
                agenciaExistente.setBairro(dadosCep.getBairro());
                agenciaExistente.setCidade(dadosCep.getLocalidade());
                agenciaExistente.setEstado(dadosCep.getUf());
            }
        } else {
            agenciaExistente.setLogradouro(dto.getLogradouro());
            agenciaExistente.setBairro(dto.getBairro());
            agenciaExistente.setCidade(dto.getCidade());
            agenciaExistente.setEstado(dto.getEstado());
        }

        agenciaExistente.setRazaoSocial(dto.getRazaoSocial());
        agenciaExistente.setNomeFantasia(dto.getNomeFantasia());
        agenciaExistente.setEmail(dto.getEmail());
        agenciaExistente.setTelefone(dto.getTelefone());
        agenciaExistente.setCelular(dto.getCelular());
        agenciaExistente.setCep(dto.getCep());
        agenciaExistente.setNumero(dto.getNumero());
        agenciaExistente.setComplemento(dto.getComplemento());
        agenciaExistente.setDataInauguracao(dto.getDataInauguracao());
        agenciaExistente.setBanco(dto.getBanco());
        agenciaExistente.setAgencia(dto.getAgencia());
        agenciaExistente.setConta(dto.getConta());
        agenciaExistente.setComissaoVendas(dto.getComissaoVendas());
        agenciaExistente.setVendasMes(dto.getVendasMes());
        agenciaExistente.setComissaoMes(dto.getComissaoMes());
        agenciaExistente.setContratosValidos(dto.getContratosValidos());

        if (dto.getAtivo() != null) {
            agenciaExistente.setAtivo(dto.getAtivo());
        }

        return agenciaMapper.toDTO(agenciaRepository.save(agenciaExistente));
    }

    @Transactional
    public void deletar(Long id) {
        Agencia agencia = agenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agência não encontrada."));
        agenciaRepository.delete(agencia);
    }
}