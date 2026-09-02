package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.HorariosBreaksDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.HorariosBreaks;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HorariosBreaksMapper {

    HorariosBreaksDTO toDTO(HorariosBreaks entidade);

    HorariosBreaks toEntity(HorariosBreaksDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(HorariosBreaksDTO dto, @MappingTarget HorariosBreaks entidade);
}