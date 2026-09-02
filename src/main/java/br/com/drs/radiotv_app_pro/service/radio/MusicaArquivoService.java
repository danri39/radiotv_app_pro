package br.com.drs.radiotv_app_pro.service.radio;

import br.com.drs.radiotv_app_pro.dto.radio.ArquivoImportacaoDTO;
import br.com.drs.radiotv_app_pro.dto.radio.MusicasXmlDTO;
import br.com.drs.radiotv_app_pro.dto.radio.MusicasXmlWrapper;
import br.com.drs.radiotv_app_pro.mapper.radio.MusicasXmlMapper;
import br.com.drs.radiotv_app_pro.model.radio.Musicas;
import br.com.drs.radiotv_app_pro.model.radio.Pastas;
import br.com.drs.radiotv_app_pro.repository.radio.MusicasRepository;
import br.com.drs.radiotv_app_pro.repository.radio.PastasRepository;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class MusicaArquivoService {

    private final XmlMapper xmlMapper;
    private final PastasRepository pastasRepository;
    private final MusicasXmlMapper musicasXmlMapper;
    private final MusicasRepository musicaRepository;



    public ArquivoImportacaoDTO lerPastaMusicas() {
        // Busca automaticamente as configurações de pastas
        List<Pastas> pastasList = pastasRepository.findAll();
        if (pastasList.isEmpty()) {
            throw new IllegalArgumentException("Configurações de pastas não encontradas no banco.");
        }

        Pastas pastas = pastasList.get(0);
        String pastaMusicas = pastas.getMusicas();
        String pastaArquivos = pastas.getArquivos();

        if (pastaMusicas == null || pastaMusicas.trim().isEmpty()) {
            throw new IllegalArgumentException("Caminho da pasta 'musicas' não configurado.");
        }
        if (pastaArquivos == null || pastaArquivos.trim().isEmpty()) {
            throw new IllegalArgumentException("Caminho da pasta 'arquivos' não configurado.");
        }

        List<MusicasXmlDTO> musicas = new ArrayList<>();
        List<String> erros = new ArrayList<>();

        try {
            Path pastaPath = Paths.get(pastaMusicas);

            if (!Files.exists(pastaPath)) {
                throw new IllegalArgumentException(STR."Pasta de músicas não encontrada: \{pastaMusicas}");
            }

            Set<String> extensoesSuportadas = Set.of(
                    "mp3", "wav", "flac", "aac", "ogg", "m4a", "wma"
            );

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(pastaPath)) {
                for (Path arquivo : stream) {
                    try {
                        if (Files.isRegularFile(arquivo) && extensoesSuportadas.contains(getExtensao(arquivo))) {
                            MusicasXmlDTO musica = extrairInformacoesArquivo(arquivo.toFile());
                            musicas.add(musica);
                        } else if (Files.isRegularFile(arquivo)) {
                            erros.add(STR."Arquivo não suportado: \{arquivo.getFileName()}");
                        }
                    } catch (Exception e) {
                        erros.add(STR."Erro ao processar \{arquivo.getFileName()}: \{e.getMessage()}");
                        log.error("Erro ao processar arquivo: {}", arquivo.getFileName(), e);
                    }
                }
            }

            String xmlContent = gerarXml(musicas);

            // Salva o XML na pasta arquivos
            Path caminhoXml = Paths.get(pastaArquivos, "Musicas.xml");
            Files.writeString(caminhoXml, xmlContent, StandardCharsets.UTF_8);

            log.info("XML salvo em: {}", caminhoXml.toString());
            log.info("Leitura concluída: {} músicas encontradas, {} erros", musicas.size(), erros.size());

            return ArquivoImportacaoDTO.builder()
                    .nomeArquivo("Musicas.xml")
                    .conteudoXml(xmlContent)
                    .totalMusicas(musicas.size() + erros.size())
                    .musicasImportadas(musicas.size())
                    .musicasComErro(erros.size())
                    .erros(erros)
                    .build();

        } catch (Exception e) {
            log.error("Erro ao ler pasta de músicas", e);
            throw new RuntimeException(STR."Erro ao ler pasta de músicas: \{e.getMessage()}", e);
        }
    }

    public ArquivoImportacaoDTO importarXmlDaPasta() {
        // Busca automaticamente as configurações de pastas
        List<Pastas> pastasList = pastasRepository.findAll();
        if (pastasList.isEmpty()) {
            throw new IllegalArgumentException("Configurações de pastas não encontradas no banco.");
        }

        Pastas pastas = pastasList.get(0);
        String pastaArquivos = pastas.getArquivos();

        if (pastaArquivos == null || pastaArquivos.trim().isEmpty()) {
            throw new IllegalArgumentException("Caminho da pasta 'arquivos' não configurado.");
        }

        try {
            Path caminhoXml = Paths.get(pastaArquivos, "Musicas.xml");

            if (!Files.exists(caminhoXml)) {
                throw new IllegalArgumentException("Arquivo Musicas.xml não encontrado na pasta: " + pastaArquivos);
            }

            String xmlContent = Files.readString(caminhoXml, StandardCharsets.UTF_8);

            // Usa o serviço de importação existente
            return importarMusicasDoXml(xmlContent);

        } catch (Exception e) {
            log.error("Erro ao importar XML da pasta", e);
            throw new RuntimeException("Erro ao importar XML: " + e.getMessage(), e);
        }
    }

    public ArquivoImportacaoDTO importarMusicasDoXml(String xmlContent) {
        List<String> erros = new ArrayList<>();
        int importadas = 0;

        try {
            MusicasXmlWrapper wrapper = xmlMapper.readValue(xmlContent, MusicasXmlWrapper.class);
            List<Musicas> musicas = musicasXmlMapper.toEntityList(wrapper.getMusicas());

            for (Musicas musica : musicas) {
                try {
                    Optional<Musicas> existente = Optional.empty();

                    if (musica.getId() != null) {
                        existente = musicaRepository.findById(musica.getId());
                    }

                    if (existente.isEmpty()) {
                        existente = musicaRepository.findByArtistaAndNomeMusica(
                                musica.getArtista(), musica.getNomeMusica());
                    }

                    if (existente.isPresent()) {
                        Musicas atual = existente.get();
                        musica.setId(atual.getId());
                        musica.setUltimaExecucao(atual.getUltimaExecucao());
                        musicaRepository.save(musica);
                    } else {
                        if (musica.getId() == null) {
                            musica.setId(System.currentTimeMillis());
                        }
                        musicaRepository.save(musica);
                    }

                    importadas++;

                } catch (Exception e) {
                    erros.add("Erro ao importar '" + musica.getArtista() + " - " + musica.getNomeMusica() + "': " + e.getMessage());
                    log.error("Erro ao importar música", e);
                }
            }

            log.info("Importação concluída: {} importadas, {} erros", importadas, erros.size());

            return ArquivoImportacaoDTO.builder()
                    .totalMusicas(musicas.size())
                    .musicasImportadas(importadas)
                    .musicasComErro(erros.size())
                    .erros(erros)
                    .build();

        } catch (Exception e) {
            log.error("Erro geral na importação", e);
            throw new RuntimeException("Erro na importação: " + e.getMessage(), e);
        }
    }

    private MusicasXmlDTO extrairInformacoesArquivo(File arquivo) {
        String artistaRaw = null;
        String nomeMusicaRaw = null;
        String compositorRaw = null;
        String anoLancamentoRaw = null;
        String generoRaw = null;
        Long duracaoMs = null;

        try {
            AudioFile audioFile = AudioFileIO.read(arquivo);
            Tag tag = audioFile.getTag();

            if (tag != null) {
                artistaRaw = tag.getFirst(FieldKey.ARTIST);
                nomeMusicaRaw = tag.getFirst(FieldKey.TITLE);
                compositorRaw = tag.getFirst(FieldKey.COMPOSER);
                anoLancamentoRaw = tag.getFirst(FieldKey.YEAR);
                generoRaw = tag.getFirst(FieldKey.GENRE);
            }

            // Extrai duração
            if (audioFile.getAudioHeader() != null) {
                duracaoMs = (long) audioFile.getAudioHeader().getTrackLength() * 1000L;
            }

        } catch (Exception e) {
            log.warn("Não foi possível ler tags do arquivo: {}", arquivo.getName());
        }

        // Fallback se não encontrou metadados
        if (artistaRaw == null || artistaRaw.trim().isEmpty()) {
            String nomeSemExtensao = removerExtensao(arquivo.getName());
            String[] partes = nomeSemExtensao.split(" - ", 2);

            if (partes.length >= 2) {
                artistaRaw = partes[0].trim();
                nomeMusicaRaw = partes[1].trim();
            } else {
                artistaRaw = "Desconhecido";
                nomeMusicaRaw = nomeSemExtensao.trim();
            }
        }

        // Converte duração para HH:mm:ss
        String tempoMusica = "";
        if (duracaoMs != null && duracaoMs > 0) {
            long segundos = duracaoMs / 1000;
            long horas = segundos / 3600;
            long minutos = (segundos % 3600) / 60;
            long segs = segundos % 60;
            tempoMusica = String.format("%02d:%02d:%02d", horas, minutos, segs);
        }

        // AQUI ESTÁ A CORREÇÃO FINAL: Usar formatarESanear em todos os campos de texto
        return MusicasXmlDTO.builder()
                .artista(MusicasXmlDTO.formatarESanear(artistaRaw))
                .nomeMusica(MusicasXmlDTO.formatarESanear(nomeMusicaRaw))
                .compositor(MusicasXmlDTO.formatarESanear(compositorRaw))
                .tempoMusica(tempoMusica)
                .anoLancamento(MusicasXmlDTO.formatarESanear(anoLancamentoRaw))
                .genero(MusicasXmlDTO.formatarESanear(generoRaw))
                .nacional(false)
                .ativa(true)
                .quantidade(0)
                .periodos("")
                .diasSemana("")
                .build();
    }

    private String gerarXml(List<MusicasXmlDTO> musicas) {
        try {
            MusicasXmlWrapper wrapper = MusicasXmlWrapper.builder()
                    .musicas(musicas)
                    .build();

            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
            return xmlMapper.writeValueAsString(wrapper);
        } catch (Exception e) {
            log.error("Erro ao gerar XML", e);
            throw new RuntimeException("Erro ao gerar XML: " + e.getMessage(), e);
        }
    }

    private String getExtensao(Path arquivo) {
        String nome = arquivo.getFileName().toString();
        int index = nome.lastIndexOf('.');
        return index > 0 ? nome.substring(index + 1).toLowerCase() : "";
    }

    private String removerExtensao(String nomeArquivo) {
        int index = nomeArquivo.lastIndexOf('.');
        return index > 0 ? nomeArquivo.substring(0, index) : nomeArquivo;
    }

    private String limparStringParaXml(String valor) {
        if (valor == null) {
            return ""; // Retorna vazio em vez de null para evitar problemas no XML
        }
        // Remove caracteres de controle inválidos para XML (incluindo \u0000)
        return valor.replaceAll("[\\x00-\\x1F\\x7F]", "").trim();
    }
}