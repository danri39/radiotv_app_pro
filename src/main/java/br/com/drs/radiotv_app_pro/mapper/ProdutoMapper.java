package br.com.drs.radiotv_app_pro.mapper;

import br.com.drs.radiotv_app_pro.dto.ProdutoDTO;
import br.com.drs.radiotv_app_pro.model.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    ProdutoDTO toDTO(Produto entidade);

    Produto toEntity(ProdutoDTO dto);

    void updateEntityFromDto(ProdutoDTO dto, @MappingTarget Produto entidade);
}