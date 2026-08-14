package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ProgramaDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Programa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProgramaMapper {

    ProgramaDTO toDTO(Programa programa);

    Programa toEntity(ProgramaDTO dto);

    void updateEntityFromDto(ProgramaDTO dto, @MappingTarget Programa programaExistente);
}
