package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.HorariosBreaksDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.HorariosBreaksMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.HorariosBreaks;
import br.com.drs.radiotv_app_pro.repository.escritorio.HorariosBreaksRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HorariosBreaksService {

    private final HorariosBreaksRepository repository;

    private final HorariosBreaksMapper mapper;

    @Transactional
    public HorariosBreaksDTO salvar(HorariosBreaksDTO dto) {
        HorariosBreaks entidade = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entidade));
    }

    public List<HorariosBreaksDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public HorariosBreaksDTO buscarPorId(Long id) {
        HorariosBreaks horarios = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuração de horários de break não encontrada com o ID: " + id));
        return mapper.toDTO(horarios);
    }

    @Transactional
    public HorariosBreaksDTO atualizar(Long id, HorariosBreaksDTO dto) {
        HorariosBreaks horariosExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuração de horários de break não encontrada com o ID: " + id));

        mapper.updateEntityFromDto(dto, horariosExistente);

        return mapper.toDTO(repository.save(horariosExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Configuração não encontrada com o ID: " + id);
        }
        repository.deleteById(id);
    }
}