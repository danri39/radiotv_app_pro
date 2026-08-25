package br.com.drs.radiotv_app_pro.mapper.radio;

import br.com.drs.radiotv_app_pro.dto.radio.RegistroComercialDTO;
import br.com.drs.radiotv_app_pro.model.radio.RegistroComercial;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RegistroComercialMapper {


    RegistroComercialDTO toDto(RegistroComercial registroComercial);

    RegistroComercial toEntity(RegistroComercialDTO dto);

    void updateEntityFromDto(RegistroComercialDTO dto, @MappingTarget RegistroComercial registroComercial);
}
