package br.com.drs.radiotv_app_pro.mapper.radio;

import br.com.drs.radiotv_app_pro.dto.radio.MusicasXmlDTO;
import br.com.drs.radiotv_app_pro.model.enuns.*;
import br.com.drs.radiotv_app_pro.model.radio.Musicas;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MusicasXmlMapper {

    @Mapping(target = "tempoMusica", source = "tempoMusica", qualifiedByName = "localTimeToString")
    @Mapping(target = "introducao", source = "introducao", qualifiedByName = "localTimeToString")
    @Mapping(target = "genero", source = "genero", qualifiedByName = "generoToString")
    @Mapping(target = "lancamento", source = "lancamento", qualifiedByName = "lancamentoToString")
    @Mapping(target = "periodos", source = "periodos", qualifiedByName = "periodosToString")
    @Mapping(target = "diasSemana", source = "diasSemana", qualifiedByName = "diasSemanaToString")
    @Mapping(target = "repetir", source = "repetir", qualifiedByName = "repeticaoToString")
    MusicasXmlDTO toXmlDto(Musicas musicas);

    @Mapping(target = "tempoMusica", source = "tempoMusica", qualifiedByName = "stringToLocalTime")
    @Mapping(target = "introducao", source = "introducao", qualifiedByName = "stringToLocalTime")
    @Mapping(target = "genero", source = "genero", qualifiedByName = "stringToGenero")
    @Mapping(target = "lancamento", source = "lancamento", qualifiedByName = "stringToLancamento")
    @Mapping(target = "periodos", source = "periodos", qualifiedByName = "stringToPeriodos")
    @Mapping(target = "diasSemana", source = "diasSemana", qualifiedByName = "stringToDiasSemana")
    @Mapping(target = "repetir", source = "repetir", qualifiedByName = "stringToRepeticao")
    @Mapping(target = "ultimaExecucao", ignore = true)
    Musicas toEntity(MusicasXmlDTO xmlDto);

    List<MusicasXmlDTO> toXmlDtoList(List<Musicas> musicas);
    List<Musicas> toEntityList(List<MusicasXmlDTO> xmlDtos);

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

    @Named("generoToString")
    default String generoToString(Genero genero) {
        return genero != null ? genero.name() : null;
    }

    @Named("stringToGenero")
    default Genero stringToGenero(String genero) {
        if (genero == null || genero.trim().isEmpty()) return null;
        try {
            return Genero.valueOf(genero.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Gênero inválido: " + genero);
        }
    }

    @Named("lancamentoToString")
    default String lancamentoToString(LancamentoMusica lancamento) {
        return lancamento != null ? lancamento.name() : null;
    }

    @Named("stringToLancamento")
    default LancamentoMusica stringToLancamento(String lancamento) {
        if (lancamento == null || lancamento.trim().isEmpty()) return null;
        try {
            return LancamentoMusica.valueOf(lancamento.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de lançamento inválido: " + lancamento);
        }
    }

    @Named("periodosToString")
    default String periodosToString(List<Periodos> periodos) {
        if (periodos == null || periodos.isEmpty()) return "";
        return periodos.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    @Named("stringToPeriodos")
    default List<Periodos> stringToPeriodos(String periodos) {
        if (periodos == null || periodos.trim().isEmpty()) return new ArrayList<>();
        return Arrays.stream(periodos.split(","))
                .map(String::trim)
                .filter(p -> !p.isEmpty())
                .map(p -> {
                    try {
                        return Periodos.valueOf(p.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Período inválido: " + p);
                    }
                })
                .collect(Collectors.toList());
    }

    @Named("diasSemanaToString")
    default String diasSemanaToString(List<DiasSemana> diasSemana) {
        if (diasSemana == null || diasSemana.isEmpty()) return "";
        return diasSemana.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    @Named("stringToDiasSemana")
    default List<DiasSemana> stringToDiasSemana(String diasSemana) {
        if (diasSemana == null || diasSemana.trim().isEmpty()) return new ArrayList<>();
        return Arrays.stream(diasSemana.split(","))
                .map(String::trim)
                .filter(d -> !d.isEmpty())
                .map(d -> {
                    try {
                        return DiasSemana.valueOf(d.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Dia da semana inválido: " + d);
                    }
                })
                .collect(Collectors.toList());
    }

    @Named("repeticaoToString")
    default String repeticaoToString(Repeticao repeticao) {
        return repeticao != null ? repeticao.name() : null;
    }

    @Named("stringToRepeticao")
    default Repeticao stringToRepeticao(String repeticao) {
        if (repeticao == null || repeticao.trim().isEmpty()) return null;
        try {
            return Repeticao.valueOf(repeticao.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de repetição inválido: " + repeticao);
        }
    }
}