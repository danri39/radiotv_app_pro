package br.com.drs.radiotv_app_pro.mapper.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.PagamentoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Pagamento;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PagamentoMapper {

    PagamentoDTO toDto(Pagamento pagamento);

    Pagamento toEntity(PagamentoDTO pagamentoDTO);

    void updateEntityFromDto(PagamentoDTO dto, @MappingTarget Pagamento pagamento);
}
