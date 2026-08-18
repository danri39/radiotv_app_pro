package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FaturamentoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Faturamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FaturamentoMapper {

    @Mapping(source = "contrato.id", target = "contratoId")
    FaturamentoDTO toDTO(Faturamento entidade);

    @Mapping(source = "contratoId", target = "contrato.id")
    Faturamento toEntity(FaturamentoDTO dto);

    @Mapping(source = "contratoId", target = "contrato.id")
    void updateEntityFromDto(FaturamentoDTO dto, @MappingTarget Faturamento entidade);
}