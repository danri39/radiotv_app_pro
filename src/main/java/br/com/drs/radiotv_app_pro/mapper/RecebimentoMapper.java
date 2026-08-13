package br.com.drs.radiotv_app_pro.mapper;

import br.com.drs.radiotv_app_pro.dto.RecebimentoDTO;
import br.com.drs.radiotv_app_pro.model.Recebimento;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RecebimentoMapper {

    RecebimentoDTO toDto(Recebimento recebimento);

    Recebimento toEntity(RecebimentoDTO dto);

    void updateEntityFromDto(RecebimentoDTO dto, @MappingTarget Recebimento recebimento);
}
