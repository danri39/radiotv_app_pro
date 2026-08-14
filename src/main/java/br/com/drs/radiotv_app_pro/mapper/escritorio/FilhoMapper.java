package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FilhoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Filho;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FilhoMapper {

    @Mapping(source = "funcionario.id", target = "funcionarioId")
    FilhoDTO toDTO(Filho filho);

    @Mapping(source = "funcionarioId", target = "funcionario.id")
    Filho toEntity(FilhoDTO dto);

    @Mapping(source = "funcionarioId", target = "funcionario.id")
    void updateEntityFromDto(FilhoDTO dto, @MappingTarget Filho filho);
}
