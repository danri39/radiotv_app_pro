package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.model.enuns.TipoPessoa;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import br.com.drs.radiotv_app_pro.util.ValidaDocumentoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final ViaCepService viaCepService; // Injetado aqui

    private void preencherEnderecoPorCep(Funcionario f) {
        if (f.getCep() != null && !f.getCep().isBlank()) {
            // Só busca se os campos principais de endereço estiverem vazios
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

    @Transactional
    public Funcionario salvar(Funcionario funcionario) {
        if (funcionarioRepository.existsByEmail(funcionario.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
        preencherEnderecoPorCep(funcionario);
        if (funcionario.getTipoPessoa() == TipoPessoa.FISICA) {
            if (!ValidaDocumentoUtil.isCPF(funcionario.getCpf())) {
                throw new IllegalArgumentException("CPF inválido por favor tente de novo.");
            }
            if (funcionarioRepository.existsByCpf(funcionario.getCpf())) {
                throw new IllegalArgumentException("CPF já cadastrado.");
            }
            funcionario.setCnpj(null);
            funcionario.setInscricao(null);
        } else if (funcionario.getTipoPessoa() == TipoPessoa.JURIDICA) {
            if (!ValidaDocumentoUtil.isCNPJ(funcionario.getCnpj())) {
                throw new IllegalArgumentException("CNPJ inválido por favor tente de novo.");
            }
            if (funcionarioRepository.existsByCnpj(funcionario.getCnpj())) {
                throw new IllegalArgumentException("CNPJ já cadastrado.");
            }
            funcionario.setCpf(null);
            funcionario.setRg(null);
        }

        funcionario.setAtivo(true);
        return funcionarioRepository.save(funcionario);
    }

    @Transactional
    public Funcionario atualizar(Long id, Funcionario dadosNovos) {
        Funcionario funcionarioExistente = funcionarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        // Se o front alterou o CEP, tenta preencher novamente antes de salvar as mudanças
        preencherEnderecoPorCep(dadosNovos);

        funcionarioExistente.setNome(dadosNovos.getNome());
        funcionarioExistente.setEmail(dadosNovos.getEmail());
        funcionarioExistente.setTelefone(dadosNovos.getTelefone());

        // Atualiza os dados de endereço preenchidos
        funcionarioExistente.setCep(dadosNovos.getCep());
        funcionarioExistente.setLogradouro(dadosNovos.getLogradouro());
        funcionarioExistente.setNumero(dadosNovos.getNumero());
        funcionarioExistente.setComplemento(dadosNovos.getComplemento());
        funcionarioExistente.setBairro(dadosNovos.getBairro());
        funcionarioExistente.setCidade(dadosNovos.getCidade());
        funcionarioExistente.setEstado(dadosNovos.getEstado());

        funcionarioExistente.setFormacao(dadosNovos.getFormacao());
        funcionarioExistente.setDemissao(dadosNovos.getDemissao());
        funcionarioExistente.setCargo(dadosNovos.getCargo());
        funcionarioExistente.setSalario(dadosNovos.getSalario());
        funcionarioExistente.setBanco(dadosNovos.getBanco());
        funcionarioExistente.setAgencia(dadosNovos.getAgencia());
        funcionarioExistente.setConta(dadosNovos.getConta());
        funcionarioExistente.setAtivo(dadosNovos.getAtivo());

        return funcionarioRepository.save(funcionarioExistente);
    }

    @Transactional(readOnly = true)
    public List<Funcionario> listarTodos() { return funcionarioRepository.findAll(); }

    @Transactional(readOnly = true)
    public Funcionario buscarPorId(Long id) {
        return funcionarioRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Não encontrado."));
    }

    @Transactional
    public void deletar(Long id) { funcionarioRepository.deleteById(id); }
}