package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.BreakRoteiroDTO;
import br.com.drs.radiotv_app_pro.dto.escritorio.RoteiroComercialDTO;
import br.com.drs.radiotv_app_pro.model.enuns.DiasSemana;
import br.com.drs.radiotv_app_pro.model.enuns.Distribuicao;
import br.com.drs.radiotv_app_pro.model.enuns.Periodos;
import br.com.drs.radiotv_app_pro.model.enuns.TempoMidia;
import br.com.drs.radiotv_app_pro.model.escritorio.ContratoMidia;
import br.com.drs.radiotv_app_pro.model.escritorio.GerarRoteiroRequest;
import br.com.drs.radiotv_app_pro.model.escritorio.HorariosBreaks;
import br.com.drs.radiotv_app_pro.model.escritorio.Programa;
import br.com.drs.radiotv_app_pro.repository.escritorio.ContratoMidiaRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FeriadoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.HorariosBreaksRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.ProgramaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GeradorRoteiroService {

    private final ContratoMidiaRepository midiaRepository;
    private final HorariosBreaksRepository horariosBreaksRepository;
    private final FeriadoRepository feriadoRepository;
    private final ProgramaRepository programaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void gerarRoteiroDoDia(GerarRoteiroRequest request) throws Exception {
        LocalDate dataAlvo = request.getData();
        DiasSemana diaSemanaInformado = request.getDiaSemana();

        DiasSemana diaSemanaReal = converterDayOfWeekParaEnum(dataAlvo.getDayOfWeek());
        if (diaSemanaReal != diaSemanaInformado) {
            throw new IllegalArgumentException(String.format(
                    "Inconsistência: A data %s corresponde a uma %s, mas foi informado %s.",
                    dataAlvo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    diaSemanaReal.getDescricao(),
                    diaSemanaInformado.getDescricao()
            ));
        }

        // 1. VERIFICAÇÃO DE FERIADO
        boolean ehFeriado = feriadoRepository.findAll().stream()
                .anyMatch(f -> f.getDataFeriado() != null && f.getDataFeriado().isEqual(dataAlvo));

        // 2. BUSCA DA GRADE DE PROGRAMAÇÃO DO DIA
        List<Programa> programasDoDia = programaRepository.findAll().stream()
                .filter(p -> p.getAtivo() != null && p.getAtivo())
                .filter(p -> {
                    if (ehFeriado) {
                        return p.getFeriados() != null && p.getFeriados();
                    } else {
                        return p.getDiasSemana() != null && p.getDiasSemana().contains(diaSemanaInformado);
                    }
                })
                .sorted(Comparator.comparing(Programa::getHoraInicio))
                .toList();

        // 3. ESTRUTURAÇÃO DINÂMICA DOS HORÁRIOS DO DIA E CAPACIDADES
        Map<LocalTime, List<ContratoMidia>> mapaRoteiro = new TreeMap<>();
        Map<LocalTime, Integer> mapaCapacidade = new TreeMap<>();
        montarGradeHorariosPorRegras(diaSemanaInformado, mapaRoteiro, mapaCapacidade);

        // 4. BUSCA DAS MÍDIAS ATIVAS DO DIA
        List<ContratoMidia> midiasDoDia = midiaRepository.findAll().stream()
                .filter(m -> m.getAtivo() != null && m.getAtivo() &&
                        !dataAlvo.isBefore(m.getDataInicio()) &&
                        !dataAlvo.isAfter(m.getDataFinal()) &&
                        m.getDiasSemana() != null && m.getDiasSemana().contains(diaSemanaInformado))
                .toList();

        // 5. SEPARAÇÃO DAS FILAS
        // Fila 1: DETERMINADOS FIXOS COM HORÁRIO EXATO
        List<ContratoMidia> determinadosHorarioFixo = midiasDoDia.stream()
                .filter(m -> m.getHorarioEspecifico() != null)
                .toList();

        // Fila 2: MÍDIAS VINCULADAS A PROGRAMAS ESPECÍFICOS (Ex: Comercial do Jornal da EP)
        List<ContratoMidia> vinculadasAPrograma = midiasDoDia.stream()
                .filter(m -> m.getHorarioEspecifico() == null && m.getPrograma() != null)
                .toList();

        // Fila 3: ROTATIVOS GERAIS DA EMISSORA (Nunca vinculados a um programa)
        List<ContratoMidia> rotativosGerais = midiasDoDia.stream()
                .filter(m -> m.getHorarioEspecifico() == null && m.getPrograma() == null && m.getDistribuicao() != Distribuicao.DETERMINADO)
                .sorted(Comparator.comparingInt((ContratoMidia m) -> m.getPrioridade() != null ? m.getPrioridade() : 50).reversed())
                .toList();

        // =========================================================================
        // ALOCAÇÃO 1: HORÁRIOS FIXOS DETERMINADOS
        // =========================================================================
        for (ContratoMidia midia : determinadosHorarioFixo) {
            LocalTime breakMaisProximo = encontrarBreakMaisProximo(mapaRoteiro.keySet(), midia.getHorarioEspecifico());
            for (int q = 0; q < midia.getQuantidade(); q++) {
                mapaRoteiro.get(breakMaisProximo).add(midia);
            }
        }

        // =========================================================================
        // ALOCAÇÃO 2: MÍDIAS DE PROGRAMAS ESPECÍFICOS (Distribuídas uniformemente nos breaks do programa)
        // =========================================================================
        for (ContratoMidia midia : vinculadasAPrograma) {
            Programa prog = midia.getPrograma();
            List<LocalTime> breaksDoPrograma = mapaRoteiro.keySet().stream()
                    .filter(t -> !t.isBefore(prog.getHoraInicio()) && t.isBefore(prog.getHoraFinal()))
                    .toList();

            if (breaksDoPrograma.isEmpty()) continue;

            int qtd = midia.getQuantidade();
            int totalBreaksProg = breaksDoPrograma.size();
            int salto = Math.max(1, totalBreaksProg / qtd);

            for (int q = 0; q < qtd; q++) {
                int centroIdeal = (q * salto) % totalBreaksProg;
                LocalTime breakEscolhido = null;

                // 1º Tenta break totalmente VAZIO dentro do programa
                for (int offset = 0; offset < totalBreaksProg; offset++) {
                    int idx = (centroIdeal + offset) % totalBreaksProg;
                    LocalTime bHora = breaksDoPrograma.get(idx);
                    List<ContratoMidia> alocados = mapaRoteiro.get(bHora);

                    if (alocados.isEmpty()) {
                        breakEscolhido = bHora;
                        break;
                    }
                }

                // 2º Tenta break sem choque concorrencial de ramo e sem mesmo anunciante
                if (breakEscolhido == null) {
                    int menorTempo = Integer.MAX_VALUE;
                    for (int offset = 0; offset < totalBreaksProg; offset++) {
                        int idx = (centroIdeal + offset) % totalBreaksProg;
                        LocalTime bHora = breaksDoPrograma.get(idx);
                        List<ContratoMidia> alocados = mapaRoteiro.get(bHora);

                        if (!comercialContemMesmoAnuncianteNoBloco(alocados, midia) &&
                                !comercialContemConcorrenteNoBloco(alocados, midia)) {
                            int tempoUsado = calcularTempoUsado(alocados);
                            if (tempoUsado < menorTempo) {
                                menorTempo = tempoUsado;
                                breakEscolhido = bHora;
                            }
                        }
                    }
                }

                // Fallback no programa
                if (breakEscolhido == null) {
                    breakEscolhido = breaksDoPrograma.get(centroIdeal);
                }

                mapaRoteiro.get(breakEscolhido).add(midia);
            }
        }

        // =========================================================================
        // ALOCAÇÃO 3: ROTATIVOS GERAIS (Totalmente blindados de programas com breaks próprios)
        // =========================================================================
        for (ContratoMidia rotativo : rotativosGerais) {
            int qtdInsercoes = rotativo.getQuantidade();
            if (qtdInsercoes <= 0) continue;

            LocalTime horaLimiteInicio = obterHoraInicioPorDistribuicao(rotativo.getDistribuicao());
            LocalTime horaLimiteFim = obterHoraFimPorDistribuicao(rotativo.getDistribuicao());

            // Bloqueia qualquer horário de programa com breaksProprios == true ou breaks == false
            List<LocalTime> janelasDisponiveis = new ArrayList<>(mapaRoteiro.keySet().stream()
                    .filter(time -> !time.isBefore(horaLimiteInicio) && time.isBefore(horaLimiteFim))
                    .filter(time -> {
                        Programa prog = encontrarProgramaNoHorario(programasDoDia, time);
                        if (prog == null) return true; // Faixa musical aceita rotativo
                        if (prog.getBreaks() != null && !prog.getBreaks()) return false;
                        if (prog.getBreaksProprios() != null && prog.getBreaksProprios()) return false; // BLINDADO
                        return true;
                    })
                    .toList());

            if (janelasDisponiveis.isEmpty()) {
                janelasDisponiveis = new ArrayList<>(mapaRoteiro.keySet().stream()
                        .filter(time -> {
                            Programa prog = encontrarProgramaNoHorario(programasDoDia, time);
                            return prog == null || prog.getBreaksProprios() == null || !prog.getBreaksProprios();
                        })
                        .toList());
            }

            int totalBreaksJanela = janelasDisponiveis.size();
            if (totalBreaksJanela == 0) continue;

            int fatorSalto = Math.max(1, totalBreaksJanela / qtdInsercoes);

            for (int q = 0; q < qtdInsercoes; q++) {
                int centroIdeal = (q * fatorSalto) % totalBreaksJanela;
                LocalTime breakEscolhido = null;

                // 1º Tenta break totalmente VAZIO
                for (int offset = 0; offset < totalBreaksJanela; offset++) {
                    int idx = (centroIdeal + offset) % totalBreaksJanela;
                    LocalTime localBreak = janelasDisponiveis.get(idx);
                    List<ContratoMidia> alocados = mapaRoteiro.get(localBreak);

                    if (alocados.isEmpty()) {
                        breakEscolhido = localBreak;
                        break;
                    }
                }

                // 2º Tenta break de MENOR TEMPO sem choques
                if (breakEscolhido == null) {
                    int menorTempo = Integer.MAX_VALUE;
                    for (int offset = 0; offset < totalBreaksJanela; offset++) {
                        int idx = (centroIdeal + offset) % totalBreaksJanela;
                        LocalTime localBreak = janelasDisponiveis.get(idx);
                        List<ContratoMidia> alocados = mapaRoteiro.get(localBreak);

                        boolean mesmoAnunciante = comercialContemMesmoAnuncianteNoBloco(alocados, rotativo);
                        boolean mesmoRamo = comercialContemConcorrenteNoBloco(alocados, rotativo);

                        if (!mesmoAnunciante && !mesmoRamo) {
                            int tempoUsado = calcularTempoUsado(alocados);
                            if (tempoUsado < menorTempo) {
                                menorTempo = tempoUsado;
                                breakEscolhido = localBreak;
                            }
                        }
                    }
                }

                // Fallback
                if (breakEscolhido == null) {
                    breakEscolhido = janelasDisponiveis.get(centroIdeal);
                }

                mapaRoteiro.get(breakEscolhido).add(rotativo);
            }
        }

        // 6. ORDENAÇÃO DOS SPOTS DENTRO DE CADA BREAK POR PRIORIDADE (01 Início, 99 Fim)
        for (List<ContratoMidia> listaBloco : mapaRoteiro.values()) {
            listaBloco.sort(Comparator.comparingInt((ContratoMidia m) -> m.getPrioridade() != null ? m.getPrioridade() : 50));
        }

        // 7. MONTAGEM DO DTO E EXPORTAÇÃO
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        List<BreakRoteiroDTO> listaBreaksDto = new ArrayList<>();

        for (Map.Entry<LocalTime, List<ContratoMidia>> entry : mapaRoteiro.entrySet()) {
            LocalTime horario = entry.getKey();
            List<ContratoMidia> midiasNoBloco = entry.getValue();

            Programa programaNoAr = encontrarProgramaNoHorario(programasDoDia, horario);
            String nomePrograma = (programaNoAr != null) ? programaNoAr.getNomePrograma() : "MUSICAL / SEM PROGRAMA";
            String observacao = null;

            if (programaNoAr != null && programaNoAr.getBreaksProprios() != null && programaNoAr.getBreaksProprios()) {
                observacao = "Breaks do programa, sem rotativo";
            }

            int capacidadeSegundos = mapaCapacidade.getOrDefault(horario, 180);
            int tempoUsadoSegundos = calcularTempoUsado(midiasNoBloco);

            List<String> identificacoes = midiasNoBloco.stream()
                    .map(ContratoMidia::getIdentificacao)
                    .toList();

            listaBreaksDto.add(BreakRoteiroDTO.builder()
                    .horario(horario.format(timeFormatter))
                    .capacidadeSegundos(capacidadeSegundos)
                    .tempoUsadoSegundos(tempoUsadoSegundos)
                    .programa(nomePrograma)
                    .observacao(observacao)
                    .comerciais(identificacoes)
                    .build());
        }

        RoteiroComercialDTO roteiroCompleto = RoteiroComercialDTO.builder()
                .diaRoteiro(dataAlvo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .breaks(listaBreaksDto)
                .build();

        exportarArquivos(dataAlvo, roteiroCompleto);
    }

    private Programa encontrarProgramaNoHorario(List<Programa> programas, LocalTime horario) {
        return programas.stream()
                .filter(p -> !horario.isBefore(p.getHoraInicio()) && horario.isBefore(p.getHoraFinal()))
                .findFirst()
                .orElse(null);
    }

    private void montarGradeHorariosPorRegras(DiasSemana dia,
                                              Map<LocalTime, List<ContratoMidia>> grade,
                                              Map<LocalTime, Integer> mapaCapacidade) {

        List<HorariosBreaks> regras = horariosBreaksRepository.findAll().stream()
                .filter(r -> r.getDiasSemana() != null && r.getDiasSemana().contains(dia))
                .toList();

        for (int hora = 0; hora < 24; hora++) {
            Periodos periodoAtual = classificarHoraEmPeriodo(hora);
            int breaksNestaHora = 4;
            int tempoMinutosBloco = 3;

            Optional<HorariosBreaks> regraPeriodo = regras.stream()
                    .filter(r -> r.getPeriodos() != null && (r.getPeriodos().contains(periodoAtual) || r.getPeriodos().contains(Periodos.TODOS)))
                    .findFirst();

            if (regraPeriodo.isPresent()) {
                if (regraPeriodo.get().getBreaksPorHora() != null && regraPeriodo.get().getBreaksPorHora() > 0) {
                    breaksNestaHora = regraPeriodo.get().getBreaksPorHora();
                }
                if (regraPeriodo.get().getTempoBreaks() != null && regraPeriodo.get().getTempoBreaks() > 0) {
                    tempoMinutosBloco = regraPeriodo.get().getTempoBreaks();
                }
            }

            int intervaloMinutos = 60 / breaksNestaHora;
            for (int b = 0; b < breaksNestaHora; b++) {
                LocalTime horario = LocalTime.of(hora, b * intervaloMinutos, 0);
                grade.put(horario, new ArrayList<>());
                mapaCapacidade.put(horario, tempoMinutosBloco * 60);
            }
        }
    }

    private int calcularTempoUsado(List<ContratoMidia> midias) {
        return midias.stream()
                .mapToInt(m -> converterTempoMidiaEmSegundos(m.getTempoMidia()))
                .sum();
    }

    private int converterTempoMidiaEmSegundos(TempoMidia tempo) {
        if (tempo == null) return 30;
        return switch (tempo) {
            case CINCO -> 5;
            case SETE -> 7;
            case DEZ -> 10;
            case QUINZE -> 15;
            case VINTE -> 20;
            case TRINTA -> 30;
            case QUARENTA -> 40;
            case QUARENTA_CINCO -> 45;
            case SESSENTA -> 60;
            case SETENTA_CINCO -> 75;
            case NOVENTA -> 90;
            case CENTO_VINTE -> 120;
        };
    }

    private Periodos classificarHoraEmPeriodo(int hora) {
        if (hora >= 0 && hora < 6) return Periodos.MADRUGADA;
        if (hora >= 6 && hora < 12) return Periodos.MANHA;
        if (hora >= 12 && hora < 18) return Periodos.TARDE;
        return Periodos.NOITE;
    }

    private void exportarArquivos(LocalDate dataAlvo, RoteiroComercialDTO roteiro) throws Exception {
        File pasta = new File("C:\\RadioTV\\RoteiroComercial");
        if (!pasta.exists()) pasta.mkdirs();

        String dataFormatada = dataAlvo.format(DateTimeFormatter.ofPattern("ddMMyyyy"));

        File arquivoJson = new File(pasta, String.format("RoteiroComercial_%s.json", dataFormatada));
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(arquivoJson, roteiro);

        File arquivoTxt = new File(pasta, String.format("RoteiroComercial_%s.txt", dataFormatada));
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoTxt, false))) {
            writer.println("=======================================================================================================================");
            writer.println("ROTEIRO COMERCIAL DIÁRIO - RADIOTV APP PRO");
            writer.printf("COMPETÊNCIA: %s%n", roteiro.getDiaRoteiro());
            writer.println("=======================================================================================================================");
            writer.printf("%-10s | %-32s | %-12s | %-10s | %-35s%n", "HORÁRIO", "PROGRAMA NO AR", "CAPACIDADE", "OCUPADO", "SPOTS ALOCADOS");
            writer.println("-----------------------------------------------------------------------------------------------------------------------");

            for (BreakRoteiroDTO b : roteiro.getBreaks()) {
                String spots = b.getComerciais().isEmpty() ? "--- VAZIO ---" : String.join(", ", b.getComerciais());
                writer.printf("%-10s | %-32s | %6ds (%02d:%02d) | %6ds (%02d:%02d) | %s%n",
                        b.getHorario(),
                        b.getPrograma(),
                        b.getCapacidadeSegundos(), b.getCapacidadeSegundos() / 60, b.getCapacidadeSegundos() % 60,
                        b.getTempoUsadoSegundos(), b.getTempoUsadoSegundos() / 60, b.getTempoUsadoSegundos() % 60,
                        spots
                );
            }
            writer.println("=======================================================================================================================");
        }
    }

    private boolean comercialContemMesmoAnuncianteNoBloco(List<ContratoMidia> alocados, ContratoMidia novaMidia) {
        if (alocados.isEmpty()) return false;
        Long contratoIdNovo = novaMidia.getContrato() != null ? novaMidia.getContrato().getId() : null;

        return alocados.stream().anyMatch(m -> {
            Long contratoIdAlocado = m.getContrato() != null ? m.getContrato().getId() : null;
            return contratoIdNovo != null && contratoIdNovo.equals(contratoIdAlocado);
        });
    }

    private boolean comercialContemConcorrenteNoBloco(List<ContratoMidia> alocados, ContratoMidia novaMidia) {
        if (alocados.isEmpty() || novaMidia.getRamoAtividade() == null) return false;
        Long idRamoNovo = novaMidia.getRamoAtividade().getId();
        return alocados.stream()
                .filter(m -> m.getRamoAtividade() != null)
                .anyMatch(m -> m.getRamoAtividade().getId().equals(idRamoNovo));
    }

    private LocalTime obterHoraInicioPorDistribuicao(Distribuicao dist) {
        if (dist == null) return LocalTime.MIN;
        return switch (dist) {
            case ROTATIVO_06_24, ROTATIVO_06_18, MANHA -> LocalTime.of(6, 0);
            case ROTATIVO_12_24, TARDE -> LocalTime.of(12, 0);
            case NOITE -> LocalTime.of(18, 0);
            case MADRUGADA -> LocalTime.of(0, 0);
            default -> LocalTime.MIN;
        };
    }

    private LocalTime obterHoraFimPorDistribuicao(Distribuicao dist) {
        if (dist == null) return LocalTime.MAX;
        return switch (dist) {
            case ROTATIVO_06_18, MANHA -> LocalTime.of(12, 0);
            case TARDE -> LocalTime.of(18, 0);
            case MADRUGADA -> LocalTime.of(6, 0);
            case NOITE -> LocalTime.of(23, 59, 59);
            default -> LocalTime.MAX;
        };
    }

    private DiasSemana converterDayOfWeekParaEnum(java.time.DayOfWeek day) {
        return switch (day) {
            case MONDAY -> DiasSemana.SEGUNDA;
            case TUESDAY -> DiasSemana.TERCA;
            case WEDNESDAY -> DiasSemana.QUARTA;
            case THURSDAY -> DiasSemana.QUINTA;
            case FRIDAY -> DiasSemana.SEXTA;
            case SATURDAY -> DiasSemana.SABADO;
            case SUNDAY -> DiasSemana.DOMINGO;
        };
    }

    private LocalTime encontrarBreakMaisProximo(Set<LocalTime> horariosDisponiveis, LocalTime horaAlvo) {
        return horariosDisponiveis.stream()
                .min(Comparator.comparingLong(h -> Math.abs(h.toSecondOfDay() - horaAlvo.toSecondOfDay())))
                .orElse(horaAlvo);
    }
}