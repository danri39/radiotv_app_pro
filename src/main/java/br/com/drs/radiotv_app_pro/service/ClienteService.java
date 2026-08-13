package br.com.drs.radiotv_app_pro.service;

import br.com.drs.radiotv_app_pro.dto.ClienteDTO;
import br.com.drs.radiotv_app_pro.mapper.ClienteMapper;
import br.com.drs.radiotv_app_pro.model.Cliente;
import br.com.drs.radiotv_app_pro.model.enuns.TipoPessoa;
import br.com.drs.radiotv_app_pro.repository.ClienteRepository;
import br.com.drs.radiotv_app_pro.util.ValidaDocumentoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper mapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public ClienteDTO salvar(ClienteDTO dto) {
        // Validação condicional com base no Tipo de Pessoa
        if (dto.getTipoPessoa() == TipoPessoa.FISICA) {
            if (!ValidaDocumentoUtil.isCPF(dto.getCpf())) {
                throw new IllegalArgumentException("CPF inválido favor acertar.");
            }
            dto.setCnpj(null);
            dto.setInscricao(null);
        } else if (dto.getTipoPessoa() == TipoPessoa.JURIDICA) {
            if (!ValidaDocumentoUtil.isCNPJ(dto.getCnpj())) {
                throw new IllegalArgumentException("CNPJ inválido favor acertar.");
            }
            dto.setCpf(null);
            dto.setRg(null);
        }

        Cliente entity = mapper.toEntity(dto);
        buscarCepEPreencherEndereco(entity);
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    public List<ClienteDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ClienteDTO buscarPorId(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
        return mapper.toDTO(cliente);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, ClienteDTO dto) {
        Cliente clienteExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));

        if (dto.getTipoPessoa() == TipoPessoa.FISICA) {
            if (!ValidaDocumentoUtil.isCPF(dto.getCpf())) {
                throw new IllegalArgumentException("CPF inválido favor acertar.");
            }
            dto.setCnpj(null);
            dto.setInscricao(null);
        } else if (dto.getTipoPessoa() == TipoPessoa.JURIDICA) {
            if (!ValidaDocumentoUtil.isCNPJ(dto.getCnpj())) {
                throw new IllegalArgumentException("CNPJ inválido favor acertar.");
            }
            dto.setCpf(null);
            dto.setRg(null);
        }

        mapper.updateEntityFromDto(dto, clienteExistente);
        buscarCepEPreencherEndereco(clienteExistente);
        return mapper.toDTO(repository.save(clienteExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Cliente não encontrado com o ID: " + id);
        }
        repository.deleteById(id);
    }

    private void buscarCepEPreencherEndereco(Cliente cliente) {
        if (cliente.getCep() != null && !cliente.getCep().trim().isEmpty()) {
            try {
                String cepLimpo = cliente.getCep().replaceAll("\\D", "");

                if (cepLimpo.length() == 8) {
                    String url = "https://viacep.com.br/ws/" + cepLimpo + "/json/";

                    Map<String, String> dadosCep = restTemplate.getForObject(url, Map.class);

                    if (dadosCep != null && !dadosCep.containsKey("erro")) {
                        // Preenche os dados apenas se o usuário mandou o campo vazio ou nulo do front
                        if (cliente.getLogradouro() == null || cliente.getLogradouro().trim().isEmpty()) {
                            cliente.setLogradouro(dadosCep.get("logradouro") != null ? dadosCep.get("logradouro").toUpperCase() : "");
                        }
                        if (cliente.getBairro() == null || cliente.getBairro().trim().isEmpty()) {
                            cliente.setBairro(dadosCep.get("bairro") != null ? dadosCep.get("bairro").toUpperCase() : "");
                        }
                        if (cliente.getCidade() == null || cliente.getCidade().trim().isEmpty()) {
                            cliente.setCidade(dadosCep.get("localidade") != null ? dadosCep.get("localidade").toUpperCase() : "");
                        }
                        if (cliente.getEstado() == null || cliente.getEstado().trim().isEmpty()) {
                            cliente.setEstado(dadosCep.get("uf") != null ? dadosCep.get("uf").toUpperCase() : "");
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao consultar o ViaCEP para o cliente: " + e.getMessage());
            }
        }
    }
}