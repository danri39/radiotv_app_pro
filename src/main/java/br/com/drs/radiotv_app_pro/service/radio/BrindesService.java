package br.com.drs.radiotv_app_pro.service.radio;

import br.com.drs.radiotv_app_pro.dto.radio.BrindesDTO;
import br.com.drs.radiotv_app_pro.mapper.radio.BrindesMapper;
import br.com.drs.radiotv_app_pro.model.radio.Brindes;
import br.com.drs.radiotv_app_pro.repository.radio.BrindesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrindesService {

    private final BrindesRepository repository;
    private final BrindesMapper mapper;

    public BrindesDTO save(BrindesDTO dto) {
        Brindes novo = mapper.toEntity(dto);
        repository.save(novo);
        return mapper.toDto(novo);
    }

    public List<Brindes> findAll() {
        return repository.findAll();
    }

    public Optional<Brindes> findById(Long id) {
        return repository.findById(id);
    }

    public BrindesDTO update(Long id, BrindesDTO dto) {
        Brindes existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brinde não cadastrado no sistema."));
        mapper.updateEntityFromDto(dto, existente);
        repository.save(existente);
        return mapper.toDto(existente);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}