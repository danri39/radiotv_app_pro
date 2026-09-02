package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ProgramaDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Programa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProgramaMapper {

    @Mapping(source = "diasSemana", target = "diasSemana")
    @Mapping(source = "nacional", target = "nacional")
    @Mapping(source = "feriados", target = "feriados")
    @Mapping(source = "breaks", target = "breaks")
    @Mapping(source = "breaksProprios", target = "breaksProprios")
    @Mapping(source = "ativo", target = "ativo")
    ProgramaDTO toDTO(Programa programa);

    @Mapping(source = "diasSemana", target = "diasSemana")
    @Mapping(source = "nacional", target = "nacional")
    @Mapping(source = "feriados", target = "feriados")
    @Mapping(source = "breaks", target = "breaks")
    @Mapping(source = "breaksProprios", target = "breaksProprios")
    @Mapping(source = "ativo", target = "ativo")
    Programa toEntity(ProgramaDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "diasSemana", target = "diasSemana")
    @Mapping(source = "nacional", target = "nacional")
    @Mapping(source = "feriados", target = "feriados")
    @Mapping(source = "breaks", target = "breaks")
    @Mapping(source = "breaksProprios", target = "breaksProprios")
    @Mapping(source = "ativo", target = "ativo")
    void updateEntityFromDto(ProgramaDTO dto, @MappingTarget Programa programaExistente);
}