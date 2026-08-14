package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ContratoMidiaDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.ContratoMidia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContratoMidiaMapper {

    @Mapping(source = "contrato.id", target = "contratoId")
    @Mapping(source = "programa.id", target = "programaId")
    @Mapping(source = "ramoAtividade.id", target = "ramoAtividadeId")
    @Mapping(source = "audiosPool", target = "audiosPool") // Força o mapeamento do pool de áudios
    ContratoMidiaDTO toDTO(ContratoMidia entidade);

    @Mapping(source = "contratoId", target = "contrato.id")
    @Mapping(source = "programaId", target = "programa.id")
    @Mapping(source = "ramoAtividadeId", target = "ramoAtividade.id")
    @Mapping(source = "audiosPool", target = "audiosPool")
    ContratoMidia toEntity(ContratoMidiaDTO dto);

    @Mapping(source = "contratoId", target = "contrato.id")
    @Mapping(source = "programaId", target = "programa.id")
    @Mapping(source = "ramoAtividadeId", target = "ramoAtividade.id")
    @Mapping(source = "audiosPool", target = "audiosPool")
    void updateEntityFromDto(ContratoMidiaDTO dto, @MappingTarget ContratoMidia entidade);
}