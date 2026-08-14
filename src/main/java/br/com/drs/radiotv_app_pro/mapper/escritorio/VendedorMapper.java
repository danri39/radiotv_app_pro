package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.VendedorDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Vendedor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VendedorMapper {

    VendedorDTO toDto(Vendedor vendedor);

    Vendedor toEntity(VendedorDTO vendedorDTO);

    void updateEntityFromDto(VendedorDTO dto, @MappingTarget Vendedor vendedor);
}
