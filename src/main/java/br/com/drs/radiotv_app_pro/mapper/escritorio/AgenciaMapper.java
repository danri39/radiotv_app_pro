package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.AgenciaDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Agencia;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AgenciaMapper {

    AgenciaDTO toDTO(Agencia agencia);

    Agencia toEntity(AgenciaDTO dto);

    void updateEntityFromDto(AgenciaDTO dto, @MappingTarget Agencia entity);
}