package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FolhaPagamentoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.FolhaPagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FolhaPagamentoMapper {

    @Mapping(source = "funcionario.id", target = "funcionarioId")
    @Mapping(source = "funcionario.nome", target = "nomeFuncionario")
    FolhaPagamentoDTO toDTO(FolhaPagamento folhaPagamento);

    @Mapping(target = "funcionario", ignore = true)
    FolhaPagamento toEntity(FolhaPagamentoDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "funcionario", ignore = true)
    void updateEntityFromDto(FolhaPagamentoDTO dto, @MappingTarget FolhaPagamento folhaPagamento);
}