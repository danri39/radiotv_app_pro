package br.com.drs.radiotv_app_pro.service.radio;

import br.com.drs.radiotv_app_pro.dto.radio.PastasDTO;
import br.com.drs.radiotv_app_pro.mapper.radio.PastasMapper;
import br.com.drs.radiotv_app_pro.model.radio.Pastas;
import br.com.drs.radiotv_app_pro.repository.radio.PastasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PastasService {

    private final PastasRepository repository;
    private final PastasMapper mapper;

    public PastasDTO salvar(PastasDTO dto) {
        Pastas pastas = mapper.toEntity(dto);
        repository.save(pastas);
        return mapper.toDto(pastas);
    }

    public List<Pastas> listarTodos() {
        return repository.findAll();
    }

    public PastasDTO atualizar(Long id, PastasDTO dto) {
        Pastas existentes = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pasta não encontrada no banco de dados."));
        mapper.updateEntityFromDto(dto, existentes);
        repository.save(existentes);
        return mapper.toDto(existentes);
    }

    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
