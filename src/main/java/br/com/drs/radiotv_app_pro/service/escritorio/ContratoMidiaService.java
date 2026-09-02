package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ContratoMidiaDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.ContratoMidiaMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Contrato;
import br.com.drs.radiotv_app_pro.model.escritorio.ContratoMidia;
import br.com.drs.radiotv_app_pro.model.escritorio.Programa;
import br.com.drs.radiotv_app_pro.model.escritorio.RamoAtividade;
import br.com.drs.radiotv_app_pro.repository.escritorio.ContratoMidiaRepository;
import jakarta.persistence.EntityNotFoundException;
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
        return mapper.toDTO(repository.save(entidade));
    }

    public List<ContratoMidiaDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ContratoMidiaDTO buscarPorId(Long id) {
        ContratoMidia midia = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mídia não encontrada com ID: " + id));
        return mapper.toDTO(midia);
    }

    @Transactional
    public ContratoMidiaDTO atualizar(Long id, ContratoMidiaDTO dto) {
        // 1. Busca a entidade existente (gerenciada pelo Hibernate)
        ContratoMidia midiaExistente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mídia não encontrada com ID: " + id));

        // 2. Atualiza campos simples diretamente
        midiaExistente.setIdentificacao(dto.getIdentificacao());
        midiaExistente.setTempoMidia(dto.getTempoMidia());
        midiaExistente.setQuantidade(dto.getQuantidade());
        midiaExistente.setPrioridade(dto.getPrioridade());
        midiaExistente.setDistribuicao(dto.getDistribuicao());
        midiaExistente.setHorarioEspecifico(dto.getHorarioEspecifico());
        midiaExistente.setDiasSemana(dto.getDiasSemana());
        midiaExistente.setDataInicio(dto.getDataInicio());
        midiaExistente.setDataFinal(dto.getDataFinal());
        midiaExistente.setAtivo(dto.getAtivo());
        midiaExistente.setTipoMidia(dto.getTipoMidia());
        midiaExistente.setTemMultiplosAudios(dto.getTemMultiplosAudios());
        midiaExistente.setAudiosPool(dto.getAudiosPool());

        // 3. Gerencia relacionamentos usando os IDs do DTO (Seguro e Correto)
        if (dto.getProgramaId() != null) {
            Programa prog = new Programa();
            prog.setId(dto.getProgramaId());
            midiaExistente.setPrograma(prog);
        } else {
            midiaExistente.setPrograma(null);
        }

        if (dto.getRamoAtividadeId() != null) {
            RamoAtividade ra = new RamoAtividade();
            ra.setId(dto.getRamoAtividadeId());
            midiaExistente.setRamoAtividade(ra);
        } else {
            midiaExistente.setRamoAtividade(null);
        }

        if (dto.getContratoId() != null) {
            Contrato c = new Contrato();
            c.setId(dto.getContratoId());
            midiaExistente.setContrato(c);
        }

        // 4. Salva a entidade já gerenciada (evita erro de Identifier altered)
        return mapper.toDTO(repository.save(midiaExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Mídia não encontrada.");
        }
        repository.deleteById(id);
    }
}