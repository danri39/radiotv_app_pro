package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.UsuarioDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioDTO toDto(Usuario usuario);

    Usuario toEntity(UsuarioDTO usuarioDTO);

    void updateEntityFromDto(UsuarioDTO dto, @MappingTarget Usuario usuario);
}
