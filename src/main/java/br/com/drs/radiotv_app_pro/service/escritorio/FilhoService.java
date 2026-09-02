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
    public List<FilhoDTO> listarPorFuncionario(String chaveUsuario) {
        return filhoRepository.findByChaveUsuario(chaveUsuario)
                .stream()
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
        if (dto.getChaveUsuario() == null) {
            throw new IllegalArgumentException("O ID do funcionário é obrigatório para vincular o filho(a).");
        }
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do filho(a) é obrigatório.");
        }

        Funcionario funcionario = (Funcionario) funcionarioRepository.findByChaveUsuario(dto.getChaveUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID: " + dto.getChaveUsuario()));

        Filho filho = filhoMapper.toEntity(dto);
        filho.setChaveUsuario(dto.getChaveUsuario());
        filho.setAtivo(true);

        return filhoMapper.toDTO(filhoRepository.save(filho));
    }

    @Transactional
    public FilhoDTO atualizar(Long id, FilhoDTO dto) {
        Filho filhoExistente = filhoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Filho(a) não encontrado para atualização."));

        // Atualiza campos de texto e atributos básicos com segurança
        filhoExistente.setNome(dto.getNome());
        filhoExistente.setCpf(dto.getCpf());
        filhoExistente.setRg(dto.getRg());
        filhoExistente.setTelefone(dto.getTelefone());
        filhoExistente.setCelular(dto.getCelular());
        filhoExistente.setDataNascimento(dto.getDataNascimento());
        filhoExistente.setSexo(dto.getSexo());
        filhoExistente.setFormacao(dto.getFormacao());
        if (dto.getAtivo() != null) {
            filhoExistente.setAtivo(dto.getAtivo());
        }

        // Atualiza o vínculo do funcionário apenas se foi informado e alterado
        if (dto.getChaveUsuario() != null &&
                (filhoExistente.getChaveUsuario() == null || !filhoExistente.getChaveUsuario().equals(dto.getChaveUsuario()))) {
            Funcionario funcionario = (Funcionario) funcionarioRepository.findByChaveUsuario(dto.getChaveUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID: " + dto.getChaveUsuario()));
            filhoExistente.setChaveUsuario(dto.getChaveUsuario());
        }

        return filhoMapper.toDTO(filhoRepository.save(filhoExistente));
    }

    @Transactional
    public void deletar(Long id) {
        Filho filho = filhoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Filho(a) não encontrado."));
        filhoRepository.delete(filho);
    }
}