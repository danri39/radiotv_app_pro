package br.com.drs.radiotv_app_pro.service;

import br.com.drs.radiotv_app_pro.dto.ContratoMidiaDTO;
import br.com.drs.radiotv_app_pro.mapper.ContratoMidiaMapper;
import br.com.drs.radiotv_app_pro.model.ContratoMidia;
import br.com.drs.radiotv_app_pro.repository.ContratoMidiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratoMidiaService {

    private final ContratoMidiaRepository repository;
    private final ContratoMidiaMapper mapper;

    @Transactional
    public ContratoMidiaDTO salvar(ContratoMidiaDTO dto) {
        ContratoMidia entidade = mapper.toEntity(dto);
        if (dto.getProgramaId() == null) {
            entidade.setPrograma(null);
        }

        if (dto.getRamoAtividadeId() == null) {
            entidade.setRamoAtividade(null);
        }

        return mapper.toDTO(repository.save(entidade));
    }

    public List<ContratoMidiaDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ContratoMidiaDTO buscarPorId(Long id) {
        ContratoMidia midia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mídia do contrato não encontrada com o ID: " + id));
        return mapper.toDTO(midia);
    }

    @Transactional
    public ContratoMidiaDTO atualizar(Long id, ContratoMidiaDTO dto) {
        ContratoMidia midiaExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mídia do contrato não encontrada com o ID: " + id));

        mapper.updateEntityFromDto(dto, midiaExistente);

        if (dto.getProgramaId() == null) {
            midiaExistente.setPrograma(null);
        }

        if (dto.getRamoAtividadeId() == null) {
            midiaExistente.setRamoAtividade(null);
        }

        return mapper.toDTO(repository.save(midiaExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Mídia do contrato não encontrada com o ID: " + id);
        }
        repository.deleteById(id);
    }
}