package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ContaBancariaDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.ContaBancaria;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContaBancariaMapper {

    ContaBancariaDTO toDto(ContaBancaria contaBancaria);

    ContaBancaria toEntity(ContaBancariaDTO dto);

    void updateEntityFromDto(ContaBancariaDTO dto, @MappingTarget ContaBancaria contaBancaria);
}