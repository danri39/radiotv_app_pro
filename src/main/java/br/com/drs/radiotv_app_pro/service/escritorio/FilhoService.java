package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FilhoDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.FilhoMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Filho;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.repository.escritorio.FilhoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilhoService {

    private final FilhoRepository filhoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final FilhoMapper filhoMapper;

    @Transactional(readOnly = true)
    public List<FilhoDTO> listarTodos() {
        return filhoRepository.findAll().stream()
                .map(filhoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FilhoDTO> listarPorFuncionario(Long funcionarioId) {
        return filhoRepository.findByFuncionarioId(funcionarioId).stream()
                .map(filhoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FilhoDTO buscarPorId(Long id) {
        Filho filho = filhoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Filho(a) não encontrado com o ID: " + id));
        return filhoMapper.toDTO(filho);
    }

    @Transactional
    public FilhoDTO salvar(FilhoDTO dto) {
        if (dto.getFuncionarioId() == null) {
            throw new IllegalArgumentException("O ID do funcionário é obrigatório para vincular o filho(a).");
        }
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do filho(a) é obrigatório.");
        }

        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID: " + dto.getFuncionarioId()));

        Filho filho = filhoMapper.toEntity(dto);
        filho.setFuncionario(funcionario);
        filho.setAtivo(true);

        return filhoMapper.toDTO(filhoRepository.save(filho));
    }

    @Transactional
    public FilhoDTO atualizar(Long id, FilhoDTO dto) {
        Filho filhoExistente = filhoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Filho(a) não encontrado para atualização."));
        filhoMapper.updateEntityFromDto(dto, filhoExistente);

        return filhoMapper.toDTO(filhoRepository.save(filhoExistente));
    }

    @Transactional
    public void deletar(Long id) {
        Filho filho = filhoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Filho(a) não encontrado."));
        filhoRepository.delete(filho);
    }
}