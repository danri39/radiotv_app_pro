package br.com.drs.radiotv_app_pro.service;

import br.com.drs.radiotv_app_pro.dto.FamiliaDTO;
import br.com.drs.radiotv_app_pro.mapper.FamiliaMapper;
import br.com.drs.radiotv_app_pro.model.Familia;
import br.com.drs.radiotv_app_pro.model.Funcionario;
import br.com.drs.radiotv_app_pro.repository.FamiliaRepository;
import br.com.drs.radiotv_app_pro.repository.FuncionarioRepository;
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
    public List<FamiliaDTO> listarPorFuncionario(Long funcionarioId) {
        return familiaRepository.findByFuncionarioId(funcionarioId).stream()
                .map(familiaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FamiliaDTO buscarPorId(Long id) {
        Familia familia = familiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Membro da família não encontrado com o ID: " + id));
        return familiaMapper.toDTO(familia);
    }

    @Transactional
    public FamiliaDTO salvar(FamiliaDTO dto) {
        if (dto.getFuncionarioId() == null) {
            throw new IllegalArgumentException("O ID do funcionário é obrigatório para vincular o familiar.");
        }
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do familiar é obrigatório.");
        }
        if(ValidaDocumentoUtil.isCPF(dto.getCpf())) {
            throw new IllegalArgumentException("CPF inválido favor acertar.");
        }

        // Garante que o funcionário informado realmente existe no banco
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID fornecido."));

        Familia familia = familiaMapper.toEntity(dto);
        familia.setFuncionario(funcionario); // Vincula a entidade real buscada do banco
        familia.setAtivo(true);

        return familiaMapper.toDTO(familiaRepository.save(familia));
    }

    @Transactional
    public FamiliaDTO atualizar(Long id, FamiliaDTO dto) {
        Familia familiaExistente = familiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Membro da família não encontrado."));

        familiaMapper.updateEntityFromDto(dto, familiaExistente);

        if (dto.getCpf() != null) {
            familiaExistente.setCpf(String.valueOf(ValidaDocumentoUtil.isCPF(dto.getCpf())));
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