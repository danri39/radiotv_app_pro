package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.VeiculosDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Veiculos;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VeiculosMapper {

    VeiculosDTO toDto(Veiculos veiculos);

    Veiculos toEntity(VeiculosDTO dto);

    void updateEntityFromDto(VeiculosDTO dto, @MappingTarget Veiculos veiculos);
}
