package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FeriadoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Feriado;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FeriadoMapper {

    FeriadoDTO toDto(Feriado feriado);

    Feriado toEntity(FeriadoDTO feriadoDTO);

    void updateEntityFromDto(FeriadoDTO dto, @MappingTarget Feriado feriado);
}
