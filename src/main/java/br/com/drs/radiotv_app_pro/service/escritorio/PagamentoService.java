package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.PagamentoDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.PagamentoMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Pagamento;
import br.com.drs.radiotv_app_pro.repository.escritorio.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository repository;
    private final PagamentoMapper mapper;

    public PagamentoDTO salvar(PagamentoDTO dto) {
        Pagamento entity = mapper.toEntity(dto);
        repository.save(entity);
        return mapper.toDto(entity);
    }

    public List<Pagamento> listar() {
        return repository.findAll();
    }

    public Optional<Pagamento> findByDescricao(String descricao) {
        return repository.findByDescricao(descricao);
    }

    public Optional<Pagamento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public PagamentoDTO atualizar(Long id, PagamentoDTO dto) {
        Pagamento existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));
        mapper.updateEntityFromDto(dto, existente);
        return mapper.toDto(existente);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}