package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FeriasDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Ferias;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FeriasMapper {

    FeriasDTO toDto(Ferias ferias);

    Ferias toEntity(FeriasDTO feriasDTO);

    void updateEntityFromDto(FeriasDTO dto, @MappingTarget Ferias ferias);
}
