package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.BeneficiosDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Beneficios;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BeneficiosMapper {

    BeneficiosDTO toDTO(Beneficios beneficios);

    Beneficios toEntity(BeneficiosDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(BeneficiosDTO dto, @MappingTarget Beneficios beneficios);
}
