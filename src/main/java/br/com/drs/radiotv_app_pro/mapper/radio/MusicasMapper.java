package br.com.drs.radiotv_app_pro.mapper.radio;

import br.com.drs.radiotv_app_pro.dto.radio.MusicasDTO;
import br.com.drs.radiotv_app_pro.model.radio.Musicas;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MusicasMapper {

    MusicasDTO toDto(Musicas musicas);

    Musicas toEntity(MusicasDTO musicasDTO);

    void updateEntityFromDto(MusicasDTO dto, @MappingTarget Musicas musicas);
}
