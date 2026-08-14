package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.RecebimentoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Recebimento;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RecebimentoMapper {

    RecebimentoDTO toDto(Recebimento recebimento);

    Recebimento toEntity(RecebimentoDTO dto);

    void updateEntityFromDto(RecebimentoDTO dto, @MappingTarget Recebimento recebimento);
}
