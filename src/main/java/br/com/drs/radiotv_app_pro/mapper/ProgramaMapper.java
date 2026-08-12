package br.com.drs.radiotv_app_pro.mapper;

import br.com.drs.radiotv_app_pro.dto.ProgramaDTO;
import br.com.drs.radiotv_app_pro.model.Programa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProgramaMapper {

    ProgramaDTO toDTO(Programa programa);

    Programa toEntity(ProgramaDTO dto);

    void updateEntityFromDto(ProgramaDTO dto, @MappingTarget Programa programaExistente);
}
