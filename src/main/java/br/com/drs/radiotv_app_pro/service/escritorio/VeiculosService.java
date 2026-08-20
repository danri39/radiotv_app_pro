package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.VeiculosDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.VeiculosMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Veiculos;
import br.com.drs.radiotv_app_pro.repository.escritorio.VeiculosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VeiculosService {

    private final VeiculosRepository repository;
    private final VeiculosMapper mapper;

    public VeiculosDTO salvar(VeiculosDTO dto)  {
        Veiculos veiculos = mapper.toEntity(dto);
        repository.save(veiculos);
        return mapper.toDto(veiculos);
    }

    public List<Veiculos> listarTodos() {
        return repository.findAll();
    }

    public Optional<Veiculos> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Optional<Veiculos> buscarPorPlaca(String placa) {
        return repository.findByPlaca(placa);
    }

    public VeiculosDTO atualizar(Long id, VeiculosDTO dto) {
        Veiculos existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não localizado em nosso banco de dados."));
        mapper.updateEntityFromDto(dto, existente);
        repository.save(existente);
        return mapper.toDto(existente);
    }

    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
