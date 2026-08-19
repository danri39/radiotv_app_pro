package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ComprasDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Compras;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ComprasMapper {

    @Mapping(source = "chaveUsuario", target = "chaveUsuario")
    @Mapping(source = "produtos.id", target = "produtoId")
    ComprasDTO toDTO(Compras entidade);

    @Mapping(source = "chaveUsuario", target = "chaveUsuario")
    @Mapping(source = "produtoId", target = "produtos.id")
    Compras toEntity(ComprasDTO dto);

    @Mapping(source = "chaveUsuario", target = "chaveUsuario")
    @Mapping(source = "produtoId", target = "produtos.id")
    void updateEntityFromDto(ComprasDTO dto, @MappingTarget Compras entidade);
}