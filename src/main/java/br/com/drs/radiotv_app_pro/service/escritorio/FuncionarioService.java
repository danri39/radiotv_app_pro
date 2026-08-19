package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FuncionarioDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.FuncionarioMapper;
import br.com.drs.radiotv_app_pro.model.enuns.TipoPessoa;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import br.com.drs.radiotv_app_pro.util.ValidaDocumentoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final FuncionarioMapper mapper;
    private final ViaCepService viaCepService;

    private void preencherEnderecoPorCep(Funcionario f) {
        if (f.getCep() != null && !f.getCep().isBlank()) {
            if (f.getLogradouro() == null || f.getLogradouro().isBlank()) {
                ViaCepService.ViaCepDTO dadosCep = viaCepService.buscarEnderecoPorCep(f.getCep());
                if (dadosCep != null) {
                    f.setLogradouro(dadosCep.getLogradouro());
                    f.setBairro(dadosCep.getBairro());
                    f.setCidade(dadosCep.getLocalidade());
                    f.setEstado(dadosCep.getUf());
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Funcionario> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Funcionario buscarPorChaveUsuario(String chaveUsuario) {
        return (Funcionario) repository.findByChaveUsuario(chaveUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com a chave: " + chaveUsuario));
    }

    @Transactional(readOnly = true)
    public Optional<Funcionario> buscarPorNomeEChave(String nome, String chaveUsuario) {
        // Corrigido para "And" para evitar o erro de PropertyReferenceException
        return repository.findByNomeAndChaveUsuario(nome, chaveUsuario);
    }

    @Transactional
    public Funcionario salvar(Funcionario funcionario) {
        if (repository.existsByEmail(funcionario.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
        preencherEnderecoPorCep(funcionario);

        if (funcionario.getTipoPessoa() == TipoPessoa.FISICA) {
            if (!ValidaDocumentoUtil.isCPF(funcionario.getCpf())) {
                throw new IllegalArgumentException("CPF inválido.");
            }
            if (repository.existsByCpf(funcionario.getCpf())) {
                throw new IllegalArgumentException("CPF já cadastrado.");
            }
            funcionario.setCnpj(null);
            funcionario.setInscricao(null);
        } else if (funcionario.getTipoPessoa() == TipoPessoa.JURIDICA) {
            if (!ValidaDocumentoUtil.isCNPJ(funcionario.getCnpj())) {
                throw new IllegalArgumentException("CNPJ inválido.");
            }
            if (repository.existsByCnpj(funcionario.getCnpj())) {
                throw new IllegalArgumentException("CNPJ já cadastrado.");
            }
            funcionario.setCpf(null);
            funcionario.setRg(null);
        }

        funcionario.setAtivo(true);
        return repository.save(funcionario);
    }

    @Transactional
    public FuncionarioDTO atualizar(String chaveUsuario, FuncionarioDTO dto) {
        Funcionario funcionarioExistente = (Funcionario) repository.findByChaveUsuario(chaveUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        Funcionario entity = mapper.updateEntityFromDto(dto, funcionarioExistente);
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Transactional
    public FuncionarioDTO inativar(String chaveUsuario) {
        Funcionario funcionario = (Funcionario) repository.findByChaveUsuario(chaveUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        funcionario.setAtivo(false);
        repository.save(funcionario);
        return mapper.toDTO(funcionario);
    }
}