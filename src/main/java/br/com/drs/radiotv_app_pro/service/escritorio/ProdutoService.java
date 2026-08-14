package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ProdutoDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.ProdutoMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Produto;
import br.com.drs.radiotv_app_pro.repository.escritorio.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository repository;

    private final ProdutoMapper mapper;

    @Transactional
    public ProdutoDTO salvar(ProdutoDTO dto) {
        Produto entidade = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entidade));
    }

    public List<ProdutoDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ProdutoDTO buscarPorId(Long id) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));
        return mapper.toDTO(produto);
    }

    @Transactional
    public ProdutoDTO atualizar(Long id, ProdutoDTO dto) {
        Produto produtoExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));

        // Mescla as alterações do DTO na entidade monitorada pelo JPA
        mapper.updateEntityFromDto(dto, produtoExistente);

        return mapper.toDTO(repository.save(produtoExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Produto não encontrado com o ID: " + id);
        }
        repository.deleteById(id);
    }
}