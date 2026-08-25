package br.com.drs.radiotv_app_pro.service.radio;

import br.com.drs.radiotv_app_pro.dto.radio.RegistroMusicalDTO;
import br.com.drs.radiotv_app_pro.mapper.radio.RegistroMusicalMapper;
import br.com.drs.radiotv_app_pro.model.radio.RegistroMusical;
import br.com.drs.radiotv_app_pro.repository.radio.RegistroMusicalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistroMusicalService {

    private final RegistroMusicalRepository repository;
    private final RegistroMusicalMapper mapper;

    public RegistroMusicalDTO salvar(RegistroMusicalDTO dto) {
        RegistroMusical novo = mapper.toEntity(dto);
        repository.save(novo);
        return mapper.toDto(novo);
    }

    public Optional<RegistroMusical> buscarPorId(String id) {
        return repository.findById(id);
    }

    public RegistroMusical atualizar(String id, RegistroMusicalDTO dto) {
        RegistroMusical registro = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(STR."Registro não encontrado para o ID: \{id}"));
        mapper.updateEntityFromDto(dto, registro);
        repository.save(registro);
        return registro;
    }

    public RegistroMusicalDTO inativar(String id, RegistroMusicalDTO dto) {
        dto.setAtivo(false);
        RegistroMusical entity = mapper.toEntity(dto);
        entity.setId(String.valueOf(id));
        RegistroMusical salvo = repository.save(entity);
        return mapper.toDto(salvo);
    }
}
