package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FamiliaDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.FamiliaMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Familia;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.repository.escritorio.FamiliaRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import br.com.drs.radiotv_app_pro.util.ValidaDocumentoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamiliaService {

    private final FamiliaRepository familiaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final FamiliaMapper familiaMapper;

    @Transactional(readOnly = true)
    public List<FamiliaDTO> listarTodos() {
        return familiaRepository.findAll().stream()
                .map(familiaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FamiliaDTO> buscarPorChaveUsuario(String chaveUsuario) {
        return familiaRepository.findByChaveUsuario(chaveUsuario).stream()
                .map(familiaMapper::toDTO)
                .toList();
    }

    @Transactional
    public FamiliaDTO salvar(FamiliaDTO dto) {
        if (dto.getChaveUsuario() == null) {
            throw new IllegalArgumentException("O ID do funcionário é obrigatório para vincular o familiar.");
        }
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do familiar é obrigatório.");
        }
        if(!ValidaDocumentoUtil.isCPF(dto.getCpf())) {
            throw new IllegalArgumentException("CPF inválido favor acertar.");
        }

        // Garante que o funcionário informado realmente existe no banco
        Funcionario funcionario = (Funcionario) funcionarioRepository.findByChaveUsuario(dto.getChaveUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID fornecido."));

        Familia familia = familiaMapper.toEntity(dto);
        familia.setChaveUsuario(dto.getChaveUsuario());
        familia.setAtivo(true);

        return familiaMapper.toDTO(familiaRepository.save(familia));
    }

    @Transactional
    public FamiliaDTO atualizar(Long id, FamiliaDTO dto) {
        Familia familiaExistente = familiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Membro da família não encontrado."));

        // Valida o CPF se ele foi alterado/informado
        if (dto.getCpf() != null && !dto.getCpf().isBlank()) {
            if (!ValidaDocumentoUtil.isCPF(dto.getCpf())) {
                throw new IllegalArgumentException("CPF inválido favor acertar.");
            }
            familiaExistente.setCpf(dto.getCpf());
        }

        // Atualiza os campos básicos permitidos
        familiaExistente.setNome(dto.getNome());
        familiaExistente.setRg(dto.getRg());
        familiaExistente.setTelefone(dto.getTelefone());
        familiaExistente.setCelular(dto.getCelular());
        familiaExistente.setDataNascimento(dto.getDataNascimento());
        familiaExistente.setSexo(dto.getSexo());
        familiaExistente.setFormacao(dto.getFormacao());
        if (dto.getAtivo() != null) {
            familiaExistente.setAtivo(dto.getAtivo());
        }

        // Se o funcionário foi alterado no DTO, atualiza o vínculo com segurança
        if (dto.getChaveUsuario() != null &&
                (familiaExistente.getChaveUsuario() == null || !familiaExistente.getChaveUsuario().equals(dto.getChaveUsuario()))) {
            Funcionario novoFuncionario = (Funcionario) funcionarioRepository.findByChaveUsuario(dto.getChaveUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID fornecido."));
            familiaExistente.setChaveUsuario(dto.getChaveUsuario());
        }

        return familiaMapper.toDTO(familiaRepository.save(familiaExistente));
    }

    @Transactional
    public void deletar(Long id) {
        Familia familia = familiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Membro da família não encontrado."));
        familiaRepository.delete(familia);
    }
}