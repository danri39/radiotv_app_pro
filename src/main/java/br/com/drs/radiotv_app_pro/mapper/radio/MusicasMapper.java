package br.com.drs.radiotv_app_pro.mapper.radio;

import br.com.drs.radiotv_app_pro.dto.radio.MusicasDTO;
import br.com.drs.radiotv_app_pro.model.radio.Musicas;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MusicasMapper {

    @Mapping(target = "tempoMusica", source = "tempoMusica", qualifiedByName = "localTimeToString")
    @Mapping(target = "introducao", source = "introducao", qualifiedByName = "localTimeToString")
    MusicasDTO toDto(Musicas musicas);

    @Mapping(target = "tempoMusica", source = "tempoMusica", qualifiedByName = "stringToLocalTime")
    @Mapping(target = "introducao", source = "introducao", qualifiedByName = "stringToLocalTime")
    Musicas toEntity(MusicasDTO musicasDTO);

    @Mapping(target = "tempoMusica", source = "tempoMusica", qualifiedByName = "stringToLocalTime")
    @Mapping(target = "introducao", source = "introducao", qualifiedByName = "stringToLocalTime")
    @Mapping(target = "id", ignore = true) // Não sobrescreve o ID
    @Mapping(target = "ultimaExecucao", ignore = true) // Mantém a última execução
    void updateEntityFromDto(MusicasDTO dto, @MappingTarget Musicas musicas);

    List<MusicasDTO> toDtoList(List<Musicas> musicas);

    @Named("localTimeToString")
    default String localTimeToString(LocalTime localTime) {
        return localTime != null ? localTime.toString() : null;
    }

    @Named("stringToLocalTime")
    default LocalTime stringToLocalTime(String time) {
        if (time == null || time.trim().isEmpty()) return null;
        try {
            return LocalTime.parse(time);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de hora inválido: " + time);
        }
    }
}
