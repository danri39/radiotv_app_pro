package br.com.drs.radiotv_app_pro.mapper;

import br.com.drs.radiotv_app_pro.dto.AgenciaDTO;
import br.com.drs.radiotv_app_pro.model.Agencia;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AgenciaMapper {

    AgenciaDTO toDTO(Agencia agencia);

    Agencia toEntity(AgenciaDTO dto);

    void updateEntityFromDto(AgenciaDTO dto, @MappingTarget Agencia entity);
}