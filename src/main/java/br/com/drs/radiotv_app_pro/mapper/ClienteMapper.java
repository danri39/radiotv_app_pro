package br.com.drs.radiotv_app_pro.mapper;

import br.com.drs.radiotv_app_pro.dto.ClienteDTO;
import br.com.drs.radiotv_app_pro.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteDTO toDTO(Cliente entidade);

    Cliente toEntity(ClienteDTO dto);

    void updateEntityFromDto(ClienteDTO dto, @MappingTarget Cliente entidade);
}