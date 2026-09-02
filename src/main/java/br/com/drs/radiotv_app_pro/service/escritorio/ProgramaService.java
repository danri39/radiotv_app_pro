package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ProgramaDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.ProgramaMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Programa;
import br.com.drs.radiotv_app_pro.repository.escritorio.ProgramaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramaService {

    private final ProgramaRepository repository;

    private final ProgramaMapper mapper;

    @Transactional
    public ProgramaDTO salvar(ProgramaDTO dto) {
        Programa entidade = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entidade));
    }

    public List<ProgramaDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ProgramaDTO buscarPorId(Long id) {
        Programa programa = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programa não encontrado"));
        return mapper.toDTO(programa);
    }

    @Transactional
    public ProgramaDTO atualizar(Long id, ProgramaDTO dto) {
        Programa programaExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programa não encontrado"));

        mapper.updateEntityFromDto(dto, programaExistente);

        return mapper.toDTO(repository.save(programaExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Programa não encontrado");
        }
        repository.deleteById(id);
    }
}