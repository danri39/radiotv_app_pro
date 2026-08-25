package br.com.drs.radiotv_app_pro.mapper.radio;

import br.com.drs.radiotv_app_pro.dto.radio.RegistroMusicalDTO;
import br.com.drs.radiotv_app_pro.model.radio.RegistroMusical;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RegistroMusicalMapper {

    RegistroMusicalDTO toDto(RegistroMusical registroMusical);

    RegistroMusical toEntity(RegistroMusicalDTO dto);

    void updateEntityFromDto(RegistroMusicalDTO dto, @MappingTarget RegistroMusical registroMusical);
}
