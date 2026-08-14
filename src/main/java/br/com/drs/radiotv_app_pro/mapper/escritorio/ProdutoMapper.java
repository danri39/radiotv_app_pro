package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ProdutoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    ProdutoDTO toDTO(Produto entidade);

    Produto toEntity(ProdutoDTO dto);

    void updateEntityFromDto(ProdutoDTO dto, @MappingTarget Produto entidade);
}