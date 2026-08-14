package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.BeneficiosDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.BeneficiosMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Beneficios;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.repository.escritorio.BeneficiosRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BeneficiosService {

    private final BeneficiosRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final BeneficiosMapper mapper;

    @Transactional
    public BeneficiosDTO salvar(BeneficiosDTO dto) {
        validarFuncionario(dto);
        Beneficios beneficios = mapper.toEntity(dto);
        associarFuncionario(dto, beneficios);
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

    public List<BeneficiosDTO> listarPorFuncionario(Long funcionarioId) {
        return repository.findByFuncionarioId(funcionarioId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional
    public BeneficiosDTO atualizar(Long id, BeneficiosDTO dto) {
        Beneficios beneficios = buscarBeneficio(id);

        validarFuncionario(dto);

        mapper.updateEntityFromDto(dto, beneficios);

        associarFuncionario(dto, beneficios);

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
                                STR."Registro de benefícios não encontrado com o ID: \{id}"
                        )
                );
    }

    private void validarFuncionario(BeneficiosDTO dto) {
        if (dto.getFuncionarioId() == null) {
            throw new IllegalArgumentException(
                    "É obrigatório associar o benefício a um funcionário."
            );
        }
    }

    private void associarFuncionario(BeneficiosDTO dto, Beneficios beneficios) {
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                STR."Funcionário não encontrado com o ID: \{dto.getFuncionarioId()}"
                        )
                );
        beneficios.setFuncionario(funcionario);
    }
}