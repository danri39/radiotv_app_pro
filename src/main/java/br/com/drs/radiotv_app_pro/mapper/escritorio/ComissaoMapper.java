package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ComissaoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Comissao;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ComissaoMapper {

    ComissaoDTO toDto(Comissao comissao);

    Comissao toEntity(ComissaoDTO comissaoDTO);

    void updateEntityFromDto(ComissaoDTO dto, @MappingTarget Comissao comissao);
}
