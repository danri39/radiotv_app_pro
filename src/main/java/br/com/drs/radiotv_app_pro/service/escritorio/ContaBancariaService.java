package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ContaBancariaDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.ContaBancariaMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.ContaBancaria;
import br.com.drs.radiotv_app_pro.repository.escritorio.ContaBancariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContaBancariaService {

    private final ContaBancariaRepository repository;

    private final ContaBancariaMapper mapper;

    @Transactional
    public ContaBancariaDTO salvar(ContaBancariaDTO dto) {
        ContaBancaria entidade = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entidade));
    }

    public List<ContaBancariaDTO> listarTodas() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public ContaBancariaDTO buscarPorId(Long id) {
        ContaBancaria conta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta bancária não encontrada com o ID: " + id));
        return mapper.toDto(conta);
    }

    @Transactional
    public ContaBancariaDTO atualizar(Long id, ContaBancariaDTO dto) {
        ContaBancaria contaExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta bancária não encontrada com o ID: " + id));

        mapper.updateEntityFromDto(dto, contaExistente);

        return mapper.toDto(repository.save(contaExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Conta bancária não encontrada com o ID: " + id);
        }
        repository.deleteById(id);
    }
}