package br.com.drs.radiotv_app_pro.mapper.radio;

import br.com.drs.radiotv_app_pro.dto.radio.OuvintesDTO;
import br.com.drs.radiotv_app_pro.model.radio.Ouvintes;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OuvintesMapper {

    OuvintesDTO toDto(Ouvintes ouvintes);

    Ouvintes toEntity(OuvintesDTO dto);

    void updateEntityFromDto(OuvintesDTO dto, @MappingTarget Ouvintes ouvintes);
}
