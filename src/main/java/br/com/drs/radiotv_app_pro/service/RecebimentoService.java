package br.com.drs.radiotv_app_pro.service;

import br.com.drs.radiotv_app_pro.dto.RecebimentoDTO;
import br.com.drs.radiotv_app_pro.mapper.RecebimentoMapper;
import br.com.drs.radiotv_app_pro.model.Recebimento;
import br.com.drs.radiotv_app_pro.repository.RecebimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecebimentoService {

    private final RecebimentoRepository repository;
    private final RecebimentoMapper mapper;

    @Transactional
    public RecebimentoDTO salvar(RecebimentoDTO dto) {
        Recebimento entity = mapper.toEntity(dto);
        repository.save(entity);
        return mapper.toDto(entity);
    }

    @Transactional
    public List<Recebimento> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Recebimento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public RecebimentoDTO atualizar(Long id, RecebimentoDTO dto) {
        Recebimento recebimentoExistente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recebimento não encontrado. "));
        recebimentoExistente = repository.save(recebimentoExistente);
        return RecebimentoDTO.builder()
                .id(recebimentoExistente.getId())
                .build();
    }

    @Transactional
    public void remover(Long id) {
        repository.deleteById(id);
    }
}