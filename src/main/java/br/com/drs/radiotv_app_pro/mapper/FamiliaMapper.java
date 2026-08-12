package br.com.drs.radiotv_app_pro.mapper;

import br.com.drs.radiotv_app_pro.dto.FamiliaDTO;
import br.com.drs.radiotv_app_pro.model.Familia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FamiliaMapper {

    @Mapping(source = "funcionario.id", target = "funcionarioId")
    FamiliaDTO toDTO(Familia familia);

    @Mapping(source = "funcionarioId", target = "funcionario.id")
    Familia toEntity(FamiliaDTO dto);

    void updateEntityFromDto(FamiliaDTO dto, @MappingTarget Familia familia);
}
