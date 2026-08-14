package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FeriadoDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.FeriadoMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Feriado;
import br.com.drs.radiotv_app_pro.repository.escritorio.FeriadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeriadoService {

    private final FeriadoRepository repository;
    private final FeriadoMapper mapper;

    public FeriadoDTO salvar(FeriadoDTO dto) {
        Feriado feriado = mapper.toEntity(dto);
        repository.save(feriado);
        return mapper.toDto(feriado);
    }

    public List<Feriado> listar() {
        return repository.findAll();
    }

    public Optional<Feriado> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public FeriadoDTO atualizar(Long id, FeriadoDTO dto) {
        Feriado existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feriado não encontrado na base de dados."));
        mapper.updateEntityFromDto(dto, existente);
        return mapper.toDto(existente);
    }

    public void apagar(Long id) {
        repository.deleteById(id);
    }
}