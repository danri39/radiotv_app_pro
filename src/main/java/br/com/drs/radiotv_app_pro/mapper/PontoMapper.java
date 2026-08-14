package br.com.drs.radiotv_app_pro.mapper;

import br.com.drs.radiotv_app_pro.dto.PontoDTO;
import br.com.drs.radiotv_app_pro.model.Ponto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PontoMapper {

    PontoDTO toDTO(Ponto ponto);

    Ponto toEntity(PontoDTO dto);

    void updateEntityFromDto(PontoDTO dto, @MappingTarget Ponto ponto);
}
