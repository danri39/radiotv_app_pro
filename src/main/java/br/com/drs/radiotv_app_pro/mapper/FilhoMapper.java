package br.com.drs.radiotv_app_pro.mapper;

import br.com.drs.radiotv_app_pro.dto.FilhoDTO;
import br.com.drs.radiotv_app_pro.model.Filho;
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
