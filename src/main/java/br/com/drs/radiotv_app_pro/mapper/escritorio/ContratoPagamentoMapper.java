package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ContratoPagamentoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.ContratoPagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContratoPagamentoMapper {

    @Mapping(source = "contrato.id", target = "contratoId")
    ContratoPagamentoDTO toDTO(ContratoPagamento entidade);

    @Mapping(source = "contratoId", target = "contrato.id")
    ContratoPagamento toEntity(ContratoPagamentoDTO dto);

    @Mapping(source = "contratoId", target = "contrato.id")
    void updateEntityFromDto(ContratoPagamentoDTO dto, @MappingTarget ContratoPagamento entidade);
}