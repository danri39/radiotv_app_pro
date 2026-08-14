package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.RamoAtividadeDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.RamoAtividade;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RamoAtividadeMapper {

    RamoAtividadeDTO toDTO(RamoAtividade ramoAtividade);

    RamoAtividade toEntity(RamoAtividadeDTO dto);

    void updateEntityFromDto(RamoAtividadeDTO dto, @MappingTarget RamoAtividade ramoAtividade);
}