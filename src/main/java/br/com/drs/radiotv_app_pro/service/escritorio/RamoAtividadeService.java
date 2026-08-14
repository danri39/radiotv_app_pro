package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.RamoAtividadeDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.RamoAtividade;
import br.com.drs.radiotv_app_pro.repository.escritorio.RamoAtividadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RamoAtividadeService {

    private final RamoAtividadeRepository ramoAtividadeRepository;

    @Transactional
    public RamoAtividadeDTO salvar(RamoAtividadeDTO dto) {
        // Validação defensiva na camada de serviço para garantir que o DTO não chegou vazio do Controller
        if (dto.getDescricao() == null || dto.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do ramo de atividade é obrigatória.");
        }

        RamoAtividade entidade = RamoAtividade.builder()
                .id(dto.getId())
                .descricao(dto.getDescricao().toUpperCase()) // Garante caixa alta também no Java
                .build();

        entidade = ramoAtividadeRepository.save(entidade);

        return RamoAtividadeDTO.builder()
                .id(entidade.getId())
                .descricao(entidade.getDescricao())
                .build();
    }

    @Transactional
    public RamoAtividadeDTO atualizar(Long id, RamoAtividadeDTO dto) {
        RamoAtividade ramoExistente = ramoAtividadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ramo de atividade não encontrado."));

        ramoExistente.setDescricao(dto.getDescricao().toUpperCase());

        ramoExistente = ramoAtividadeRepository.save(ramoExistente);

        return RamoAtividadeDTO.builder()
                .id(ramoExistente.getId())
                .descricao(ramoExistente.getDescricao())
                .build();
    }

    @Transactional(readOnly = true)
    public List<RamoAtividadeDTO> listarTodos() {
        return ramoAtividadeRepository.findAll().stream()
                .map(entidade -> RamoAtividadeDTO.builder()
                        .id(entidade.getId())
                        .descricao(entidade.getDescricao())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void deletar(Long id) {
        RamoAtividade ramo = ramoAtividadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ramo de atividade não encontrado."));
        ramoAtividadeRepository.delete(ramo);
    }
}