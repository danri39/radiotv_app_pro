package br.com.drs.radiotv_app_pro.service.radio;

import br.com.drs.radiotv_app_pro.dto.radio.OuvintesDTO;
import br.com.drs.radiotv_app_pro.mapper.radio.OuvintesMapper;
import br.com.drs.radiotv_app_pro.model.radio.Ouvintes;
import br.com.drs.radiotv_app_pro.repository.radio.OuvintesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OuvintesService {

    private final OuvintesRepository repository;
    private final OuvintesMapper mapper;

    public OuvintesDTO save(OuvintesDTO dto) {
        Ouvintes novo = mapper.toEntity(dto);
        repository.save(novo);
        return mapper.toDto(novo);
    }

    public List<Ouvintes> findAll() {
        return repository.findAll();
    }

    public Optional<Ouvintes> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Ouvintes> findByNome(String nome) {
        return repository.findByNome(nome);
    }

    public OuvintesDTO update(Long id, OuvintesDTO dto) {
        Ouvintes existentes = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ouvinte não encontrado com o id: " +id));
        mapper.updateEntityFromDto(dto, existentes);
        repository.save(existentes);
        return mapper.toDto(existentes);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}