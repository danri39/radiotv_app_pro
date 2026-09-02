package br.com.drs.radiotv_app_pro.mapper.radio;

import br.com.drs.radiotv_app_pro.dto.radio.BrindesDTO;
import br.com.drs.radiotv_app_pro.model.radio.Brindes;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrindesMapper {

    BrindesDTO toDto(Brindes brindes);

    Brindes toEntity(BrindesDTO brindesDTO);

    void updateEntityFromDto(BrindesDTO dto, @MappingTarget Brindes brindes);
}
