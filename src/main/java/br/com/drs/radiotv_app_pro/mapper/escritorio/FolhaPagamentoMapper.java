package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FolhaPagamentoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.FolhaPagamento;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FolhaPagamentoMapper {

    FolhaPagamentoDTO toDTO(FolhaPagamento folhaPagamento);

    FolhaPagamento toEntity(FolhaPagamentoDTO dto);

    void updateEntityFromDto(FolhaPagamentoDTO dto, @MappingTarget FolhaPagamento folhaPagamento);
}