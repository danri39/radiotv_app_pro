package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ConfigEscritorioDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.ConfigEscritorio;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConfigEscritorioMapper {

    ConfigEscritorioDTO toDto(ConfigEscritorio configEscritorio);

    ConfigEscritorio toEntity(ConfigEscritorioDTO dto);

    void updateEntityFromDto(ConfigEscritorioDTO dto, @MappingTarget ConfigEscritorio configEscritorio);
}
