package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.PontoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Ponto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PontoMapper {

    PontoDTO toDTO(Ponto ponto);

    Ponto toEntity(PontoDTO dto);

    void updateEntityFromDto(PontoDTO dto, @MappingTarget Ponto ponto);
}
