package br.com.drs.radiotv_app_pro.service.radio;

import br.com.drs.radiotv_app_pro.dto.radio.PastasDTO;
import br.com.drs.radiotv_app_pro.mapper.radio.PastasMapper;
import br.com.drs.radiotv_app_pro.model.radio.Pastas;
import br.com.drs.radiotv_app_pro.repository.radio.PastasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PastasService {

    private final PastasRepository repository;
    private final PastasMapper mapper;

    public PastasDTO save(PastasDTO dto) {
        Pastas pastasNovas = mapper.toEntity(dto);
        repository.save(pastasNovas);
        return mapper.toDto(pastasNovas);
    }

    public List<Pastas> listAll() {
        return repository.findAll();
    }

    public Optional<Pastas> findById(Long id) {
        return repository.findById(id);
    }

    public PastasDTO update(Long id, PastasDTO dto) {
        Pastas existentes = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pasta não encontrada no sistema."));
        mapper.updateEntityFromDto(dto, existentes);
        repository.save(existentes);
        return mapper.toDto(existentes);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}