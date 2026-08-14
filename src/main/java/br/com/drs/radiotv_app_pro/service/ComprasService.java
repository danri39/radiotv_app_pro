package br.com.drs.radiotv_app_pro.service;

import br.com.drs.radiotv_app_pro.dto.ComprasDTO;
import br.com.drs.radiotv_app_pro.mapper.ComprasMapper;
import br.com.drs.radiotv_app_pro.model.Compras;
import br.com.drs.radiotv_app_pro.repository.ComprasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComprasService {

    private final ComprasRepository repository;

    private final ComprasMapper mapper;

    @Transactional
    public ComprasDTO salvar(ComprasDTO dto) {
        Compras entidade = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entidade));
    }

    public List<ComprasDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ComprasDTO buscarPorId(Long id) {
        Compras compra = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada com o ID: " + id));
        return mapper.toDTO(compra);
    }

    @Transactional
    public ComprasDTO atualizar(Long id, ComprasDTO dto) {
        Compras compraExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada com o ID: " + id));

        mapper.updateEntityFromDto(dto, compraExistente);

        return mapper.toDTO(repository.save(compraExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Compra não encontrada com o ID: " + id);
        }
        repository.deleteById(id);
    }
}