package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ContratoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Contrato;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContratoMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "vendedor.id", target = "vendedorId")
    @Mapping(source = "agencia.id", target = "agenciaId")
    @Mapping(source = "cliente.nomeFantasia", target = "clienteNomeFantasia")
    @Mapping(source = "agencia.nomeFantasia", target = "agenciaNomeFantasia")
    ContratoDTO toDTO(Contrato entity);

    @Mapping(source = "clienteId", target = "cliente.id")
    @Mapping(source = "vendedorId", target = "vendedor.id")
    @Mapping(source = "agenciaId", target = "agencia.id")
    Contrato toEntity(ContratoDTO dto);

    void updateEntityFromDto(ContratoDTO dto, @MappingTarget Contrato contrato);
}