package br.com.drs.radiotv_app_pro.service;

import br.com.drs.radiotv_app_pro.dto.VendedorDTO;
import br.com.drs.radiotv_app_pro.mapper.VendedorMapper;
import br.com.drs.radiotv_app_pro.model.Vendedor;
import br.com.drs.radiotv_app_pro.repository.VendedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendedorService {

    private final VendedorRepository repository;
    private final VendedorMapper mapper;

    public VendedorDTO salvar(VendedorDTO dto) {
        Vendedor entity = mapper.toEntity(dto);
        repository.save(entity);
        return mapper.toDto(entity);
    }

    public List<Vendedor> listarTodos() {
        return repository.findAll();
    }

    public Optional<Vendedor> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public VendedorDTO atualizar(Long id, VendedorDTO dto) {
        Vendedor vendedorExistente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vnededor não encontrado nabase de dados."));
        mapper.updateEntityFromDto(dto, vendedorExistente);
        return mapper.toDto(vendedorExistente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}