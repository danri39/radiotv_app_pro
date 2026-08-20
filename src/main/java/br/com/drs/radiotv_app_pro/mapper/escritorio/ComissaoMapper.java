package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ComissaoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Comissao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ComissaoMapper {

    @Mapping(source = "contrato.id", target = "contratoId")
    @Mapping(source = "agencia.id", target = "agenciaId")
    @Mapping(source = "agencia.nomeFantasia", target = "nomeAgencia")
    ComissaoDTO toDto(Comissao comissao);

    @Mapping(source = "contratoId", target = "contrato.id")
    @Mapping(source = "agenciaId", target = "agencia.id")
    Comissao toEntity(ComissaoDTO comissaoDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "contratoId", target = "contrato.id")
    @Mapping(source = "agenciaId", target = "agencia.id")
    void updateEntityFromDto(ComissaoDTO dto, @MappingTarget Comissao comissao);
}