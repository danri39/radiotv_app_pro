package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FrotaDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Frota;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FrotaMapper {

    FrotaDTO toDto(Frota frota);

    Frota toEntity(FrotaDTO frotaDTO);

    void updateEntityFromDto(FrotaDTO frotaDTO, @MappingTarget Frota frota);
}
