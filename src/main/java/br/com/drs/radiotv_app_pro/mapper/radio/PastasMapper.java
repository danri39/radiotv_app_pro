package br.com.drs.radiotv_app_pro.mapper.radio;

import br.com.drs.radiotv_app_pro.dto.radio.PastasDTO;
import br.com.drs.radiotv_app_pro.model.radio.Pastas;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PastasMapper {

    PastasDTO toDto(Pastas pastas);

    Pastas toEntity(PastasDTO pastasDTO);

    void updateEntityFromDto(PastasDTO dto, @MappingTarget Pastas pastas);
}
