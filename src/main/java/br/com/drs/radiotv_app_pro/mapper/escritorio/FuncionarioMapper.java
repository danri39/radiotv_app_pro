package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FuncionarioDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FuncionarioMapper {

    FuncionarioDTO toDTO(Funcionario funcionario);

    Funcionario toEntity(FuncionarioDTO dto);

    void updateEntityFromDto(FuncionarioDTO dto, @MappingTarget Funcionario funcionario);
}
