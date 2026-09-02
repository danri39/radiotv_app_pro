package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FilhoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Filho;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FilhoMapper {

    @Mapping(source = "chaveUsuario", target = "chaveUsuario")
    FilhoDTO toDTO(Filho filho);

    @Mapping(source = "chaveUsuario", target = "chaveUsuario")
    Filho toEntity(FilhoDTO dto);

    @Mapping(source = "chaveUsuario", target = "chaveUsuario")
    void updateEntityFromDto(FilhoDTO dto, @MappingTarget Filho filho);
}
