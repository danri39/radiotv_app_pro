package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ConfigEscritorioDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.ConfigEscritorioMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.ConfigEscritorio;
import br.com.drs.radiotv_app_pro.repository.escritorio.ConfigEscritorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfigEscritorioService {

    private final ConfigEscritorioRepository repository;
    private final ConfigEscritorioMapper mapper;

    public ConfigEscritorioDTO salvar(ConfigEscritorioDTO dto) {
        ConfigEscritorio entity = mapper.toEntity(dto);
        repository.save(entity);
        return mapper.toDto(entity);
    }

    public Optional<ConfigEscritorio> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public ConfigEscritorioDTO atualizar(@PathVariable Long id, @RequestBody ConfigEscritorioDTO dto) {
        ConfigEscritorio ConfigExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrado com o ID: " + id));

        mapper.updateEntityFromDto(dto, ConfigExistente);
        return mapper.toDto(ConfigExistente);
    }

    public void apagar(Long id) {
        repository.deleteById(id);
    }
}