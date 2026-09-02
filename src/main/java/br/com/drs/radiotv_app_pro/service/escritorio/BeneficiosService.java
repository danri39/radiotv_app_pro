package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.BeneficiosDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.BeneficiosMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Beneficios;
import br.com.drs.radiotv_app_pro.repository.escritorio.BeneficiosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BeneficiosService {

    private final BeneficiosRepository repository;
    private final BeneficiosMapper mapper;

    @Transactional
    public BeneficiosDTO salvar(BeneficiosDTO dto) {
        Beneficios beneficios = mapper.toEntity(dto);
        Beneficios salvo = repository.save(beneficios);
        return mapper.toDTO(salvo);
    }

    public List<BeneficiosDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public BeneficiosDTO buscarPorId(Long id) {
        Beneficios beneficios = buscarBeneficio(id);
        return mapper.toDTO(beneficios);
    }

    @Transactional
    public BeneficiosDTO atualizar(Long id, BeneficiosDTO dto) {
        Beneficios beneficios = buscarBeneficio(id);

        mapper.updateEntityFromDto(dto, beneficios);

        Beneficios atualizado = repository.save(beneficios);
        return mapper.toDTO(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        Beneficios beneficios = buscarBeneficio(id);
        repository.delete(beneficios);
    }

    private Beneficios buscarBeneficio(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                STR."Registro de benefício não encontrado com o ID: \{id}"
                        )
                );
    }
}