package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FuncionarioBeneficioDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.FuncionarioBeneficio;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FuncionarioBeneficioMapper {

    FuncionarioBeneficioDTO toDto(FuncionarioBeneficio entity);

    FuncionarioBeneficio toEntity(FuncionarioBeneficioDTO dto);

    void updateEntityFromDto(FuncionarioBeneficioDTO dto, @MappingTarget FuncionarioBeneficio entity);
}
