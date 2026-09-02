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
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeradorRoteiroService {

    private static final Logger log = LoggerFactory.getLogger(GeradorRoteiroService.class);

    private final ContratoMidiaRepository midiaRepository;
    private final HorariosBreaksRepository horariosBreaksRepository;
    private final FeriadoRepository feriadoRepository;
    private final ProgramaRepository programaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void gerarRoteiroDoDia(GerarRoteiroRequest request) throws Exception {
        LocalDate dataAlvo = request.getData();
        DiasSemana diaSemanaInformado = request.getDiaSemana();

        // Validação de consistência entre data e dia da semana informado
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

        log.info("========================================================");
        log.info(">>> GERANDO ROTEIRO PARA: {} | DATA: {} | FERIADO: {}", diaSemanaInformado, dataAlvo, ehFeriado);
        log.info("========================================================");

        // 2. BUSCA DA GRADE DE PROGRAMAÇÃO - SOLUÇÃO DEFINITIVA
        List<Programa> todosProgramas = programaRepository.findAll();
        List<Programa> programasDoDia = new ArrayList<>();

        for (Programa p : todosProgramas) {
            if (p.getAtivo() == null || !p.getAtivo()) {
                continue;
            }

            boolean aceitar = false;

            // TRAVA DE SEGURANÇA ABSOLUTA: Bloqueia programas com "Domingo" no nome se não for Domingo
            // Isso resolve o problema independentemente de como o banco de dados está configurado
            if (p.getNomePrograma().toLowerCase().contains("domingo") && diaSemanaInformado != DiasSemana.DOMINGO) {
                log.warn("[BLOQUEIO DIRETO] Programa '{}' ignorado pois hoje é {}", p.getNomePrograma(), diaSemanaInformado);
                continue;
            }

            if (ehFeriado) {
                if (Boolean.TRUE.equals(p.getFeriados())) {
                    aceitar = true;
                    log.info("[ACEITO] {} (Motivo: Feriado)", p.getNomePrograma());
                }
            } else {
                // Tenta carregar os dias do banco para garantir que não venha vazio
                try {
                    Hibernate.initialize(p.getDiasSemana());
                } catch (Exception e) {
                    log.warn("Erro ao inicializar dias do programa {}: {}", p.getNomePrograma(), e.getMessage());
                }

                List<DiasSemana> dias = p.getDiasSemana();

                // Lógica estrita: Só aceita se a lista tiver o dia exato
                if (dias != null && !dias.isEmpty() && dias.contains(diaSemanaInformado)) {
                    aceitar = true;
                    log.info("[ACEITO] {} (Motivo: Dia {} encontrado na lista {})", p.getNomePrograma(), diaSemanaInformado, dias);
                } else {
                    log.debug("[REJEITADO] {} (Dias no banco: {})", p.getNomePrograma(), dias);
                }
            }

            if (aceitar) {
                programasDoDia.add(p);
            }
        }

        // Ordena por hora de início
        programasDoDia.sort(Comparator.comparing(Programa::getHoraInicio));

        log.info(">>> TOTAL DE PROGRAMAS NA GRADE FINAL: {}", programasDoDia.size());
        for (Programa p : programasDoDia) {
            log.info("    - {} às {} : {}", p.getHoraInicio(), p.getHoraFinal(), p.getNomePrograma());
        }
        log.info("========================================================");

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
        List<ContratoMidia> determinadosHorarioFixo = midiasDoDia.stream()
                .filter(m -> m.getHorarioEspecifico() != null)
                .toList();

        List<ContratoMidia> vinculadasAPrograma = midiasDoDia.stream()
                .filter(m -> m.getHorarioEspecifico() == null && m.getPrograma() != null)
                .toList();

        List<ContratoMidia> rotativosGerais = midiasDoDia.stream()
                .filter(m -> m.getHorarioEspecifico() == null && m.getPrograma() == null && m.getDistribuicao() != Distribuicao.DETERMINADO)
                .sorted(Comparator.comparingInt((ContratoMidia m) -> m.getPrioridade() != null ? m.getPrioridade() : 50).reversed())
                .toList();

        // =========================================================================
        // ALOCAÇÃO 1: HORÁRIOS FIXOS DETERMINADOS
        // =========================================================================
        for (ContratoMidia midia : determinadosHorarioFixo) {
            LocalTime breakMaisProximo = encontrarBreakMaisProximo(mapaRoteiro.keySet(), midia.getHorarioEspecifico());

            // Verifica se o programa vinculado está na grade de hoje
            if (midia.getPrograma() != null) {
                boolean programaAtivoHoje = programasDoDia.stream()
                        .anyMatch(p -> p.getId().equals(midia.getPrograma().getId()));

                if (!programaAtivoHoje) {
                    continue; // Pula se o programa não estiver no ar (ex: feriado ou dia errado)
                }
            }

            for (int q = 0; q < midia.getQuantidade(); q++) {
                mapaRoteiro.get(breakMaisProximo).add(midia);
            }
        }

        // =========================================================================
        // ALOCAÇÃO 2: MÍDIAS DE PROGRAMAS ESPECÍFICOS
        // =========================================================================
        for (ContratoMidia midia : vinculadasAPrograma) {
            Programa prog = midia.getPrograma();

            // Verifica se o programa existe na grade deste dia
            boolean programaExisteHoje = programasDoDia.stream()
                    .anyMatch(p -> p.getId().equals(prog.getId()));

            if (!programaExisteHoje) {
                continue;
            }

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

                // 2º Tenta break sem choque concorrencial e sem mesmo anunciante
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

                if (breakEscolhido == null) {
                    breakEscolhido = breaksDoPrograma.get(centroIdeal);
                }

                mapaRoteiro.get(breakEscolhido).add(midia);
            }
        }

        // =========================================================================
        // ALOCAÇÃO 3: ROTATIVOS GERAIS
        // =========================================================================
        for (ContratoMidia rotativo : rotativosGerais) {
            int qtdInsercoes = rotativo.getQuantidade();
            if (qtdInsercoes <= 0) continue;

            LocalTime horaLimiteInicio = obterHoraInicioPorDistribuicao(rotativo.getDistribuicao());
            LocalTime horaLimiteFim = obterHoraFimPorDistribuicao(rotativo.getDistribuicao());

            List<LocalTime> janelasDisponiveis = mapaRoteiro.keySet().stream()
                    .filter(time -> !time.isBefore(horaLimiteInicio) && time.isBefore(horaLimiteFim))
                    .filter(time -> {
                        Programa prog = encontrarProgramaNoHorario(programasDoDia, time);
                        if (prog == null) return true;
                        if (Boolean.FALSE.equals(prog.getBreaks())) return false;
                        if (Boolean.TRUE.equals(prog.getBreaksProprios())) return false;
                        return true;
                    })
                    .collect(Collectors.toList());

            if (janelasDisponiveis.isEmpty()) {
                janelasDisponiveis = mapaRoteiro.keySet().stream()
                        .filter(time -> {
                            Programa prog = encontrarProgramaNoHorario(programasDoDia, time);
                            return prog == null || !Boolean.TRUE.equals(prog.getBreaksProprios());
                        })
                        .collect(Collectors.toList());
            }

            int totalBreaksJanela = janelasDisponiveis.size();
            if (totalBreaksJanela == 0) continue;

            int fatorSalto = Math.max(1, totalBreaksJanela / qtdInsercoes);

            for (int q = 0; q < qtdInsercoes; q++) {
                int centroIdeal = (q * fatorSalto) % totalBreaksJanela;
                LocalTime breakEscolhido = null;

                for (int offset = 0; offset < totalBreaksJanela; offset++) {
                    int idx = (centroIdeal + offset) % totalBreaksJanela;
                    LocalTime localBreak = janelasDisponiveis.get(idx);
                    List<ContratoMidia> alocados = mapaRoteiro.get(localBreak);

                    if (alocados.isEmpty()) {
                        breakEscolhido = localBreak;
                        break;
                    }
                }

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

                if (breakEscolhido == null) {
                    breakEscolhido = janelasDisponiveis.get(centroIdeal);
                }

                mapaRoteiro.get(breakEscolhido).add(rotativo);
            }
        }

        // 6. ORDENAÇÃO DOS SPOTS DENTRO DE CADA BREAK POR PRIORIDADE
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

            if (programaNoAr != null && Boolean.TRUE.equals(programaNoAr.getBreaksProprios())) {
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