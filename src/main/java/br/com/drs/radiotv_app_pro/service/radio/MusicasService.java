package br.com.drs.radiotv_app_pro.service.radio;

import br.com.drs.radiotv_app_pro.dto.radio.MusicasDTO;
import br.com.drs.radiotv_app_pro.mapper.radio.MusicasMapper;
import br.com.drs.radiotv_app_pro.model.radio.Musicas;
import br.com.drs.radiotv_app_pro.model.radio.Pastas;
import br.com.drs.radiotv_app_pro.repository.radio.MusicasRepository;
import br.com.drs.radiotv_app_pro.repository.radio.PastasRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicasService {

    private final MusicasRepository repository;
    private final MusicasMapper mapper;
    private final PastasRepository pastasRepository;
    private final ObjectMapper objectMapper;

    public void escanearPastaESincronizarTxt() {
        Pastas pastaConfigurada = pastasRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Diretório de pastas não configurado na tabela Pastas."));

        if (pastaConfigurada.getMusicas() == null || pastaConfigurada.getMusicas().isBlank()) {
            throw new IllegalArgumentException("O caminho da pasta de Músicas não foi configurado.");
        }

        File pastaMusicas = new File(pastaConfigurada.getMusicas());
        if (!pastaMusicas.exists() || !pastaMusicas.isDirectory()) {
            throw new IllegalArgumentException("O caminho para a pasta de músicas não existe ou é inválido: " + pastaConfigurada.getMusicas());
        }

        File[] arquivos = pastaMusicas.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".mp3") ||
                        name.toLowerCase().endsWith(".wav") ||
                        name.toLowerCase().endsWith(".wma"));

        if (arquivos != null) {
            List<Musicas> novasMusicas = new ArrayList<>();

            for (File arquivo : arquivos) {
                boolean jaExiste = repository.findAll().stream()
                        .anyMatch(m -> m.getCaminhoArquivo() != null && m.getCaminhoArquivo().equalsIgnoreCase(arquivo.getAbsolutePath()));

                if (!jaExiste) {
                    String nomeSemExtensao = arquivo.getName().replaceAll("\\.[^.]+$", "");

                    String artista = null;
                    String nomeMusica = nomeSemExtensao;

                    if (nomeSemExtensao.contains(" - ")) {
                        String[] partes = nomeSemExtensao.split(" - ", 2);
                        artista = partes[0].trim();
                        nomeMusica = partes[1].trim();
                    }

                    Musicas musica = Musicas.builder()
                            .artista(artista)
                            .nomeMusica(nomeMusica)
                            .caminhoArquivo(arquivo.getAbsolutePath())
                            .tempoMusica(LocalTime.of(0, 3, 0))
                            .ativo(true)
                            .build();
                    novasMusicas.add(musica);
                }
            }

            if (!novasMusicas.isEmpty()) {
                repository.saveAll(novasMusicas);
            }

            String diretorioDestino = (pastaConfigurada.getArquivos() != null && !pastaConfigurada.getArquivos().isBlank())
                    ? pastaConfigurada.getArquivos()
                    : pastaConfigurada.getMusicas();

            atualizarArquivoEspelhoTxt(diretorioDestino);
        }
    }

    public void restaurarBancoDoArquivoTxt() {
        Pastas pastaConfigurada = pastasRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Diretório de músicas não configurado na tabela Pastas."));

        Path caminhoTxt = Paths.get(pastaConfigurada.getMusicas(), "catalogo_musicas_backup.txt");

        if (!Files.exists(caminhoTxt)) {
            throw new EntityNotFoundException("Arquivo TXT de backup não encontrado no diretório das músicas.");
        }

        try {
            String jsonContent = Files.readString(caminhoTxt);
            List<MusicasDTO> dtosRestaurados = objectMapper.readValue(jsonContent, new TypeReference<List<MusicasDTO>>() {});

            repository.deleteAll();
            List<Musicas> entidades = dtosRestaurados.stream()
                    .map(mapper::toEntity)
                    .collect(Collectors.toList());

            repository.saveAll(entidades);
        } catch (IOException e) {
            throw new RuntimeException(STR."Erro ao ler/restaurar o arquivo TXT de backup: \{e.getMessage()}");
        }
    }

    private synchronized void atualizarArquivoEspelhoTxt(String diretorioDestino) {
        try {
            File pastaDestino = new File(diretorioDestino);
            if (!pastaDestino.exists()) {
                pastaDestino.mkdirs();
            }

            List<MusicasDTO> todas = listar();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < todas.size(); i++) {
                MusicasDTO m = todas.get(i);

                String numeroSequencial = String.format("%04d", i + 1);
                String artista = m.getArtista();
                String nomeMusica = m.getNomeMusica();

                sb.append(numeroSequencial).append(" - ");

                if (artista != null && !artista.isBlank()) {
                    sb.append(artista).append(" - ");
                }

                sb.append(nomeMusica);

                if (nomeMusica != null && !nomeMusica.toLowerCase().endsWith(".mp3")) {
                    sb.append(".mp3");
                }

                sb.append("\n");
            }

            Path caminhoTxt = Paths.get(diretorioDestino, "Musicas.txt");
            Files.writeString(caminhoTxt, sb.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (Exception e) {
            System.err.println(STR."Erro ao sincronizar arquivo TXT espelho: \{e.getMessage()}");
        }
    }

    private void sincronizarTxtSeConfigurado() {
        pastasRepository.findAll().stream().findFirst().ifPresent(pastas -> {
            String destino = (pastas.getArquivos() != null && !pastas.getArquivos().isBlank())
                    ? pastas.getArquivos()
                    : pastas.getMusicas();
            if (destino != null && !destino.isBlank()) {
                atualizarArquivoEspelhoTxt(destino);
            }
        });
    }

    // --- CRUD ---

    public MusicasDTO salvar(MusicasDTO dto) {
        Musicas musicaNova = mapper.toEntity(dto);
        musicaNova = repository.save(musicaNova);
        MusicasDTO musicaSalva = mapper.toDto(musicaNova);

        sincronizarTxtSeConfigurado();
        return musicaSalva;
    }

    public List<MusicasDTO> listar() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<MusicasDTO> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    public Optional<MusicasDTO> buscarPorNome(String nome) {
        return repository.findByNomeMusica(nome).map(mapper::toDto);
    }

    public Optional<MusicasDTO> buscarPorArtista(String artista) {
        return repository.findByArtista(artista).map(mapper::toDto);
    }

    public MusicasDTO atualizar(Long id, MusicasDTO dto) {
        Musicas musicaExistente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(STR."Música não encontrada com ID: \{id}"));

        mapper.updateEntityFromDto(dto, musicaExistente);
        Musicas atualizada = repository.save(musicaExistente);
        MusicasDTO dtoAtualizado = mapper.toDto(atualizada);

        sincronizarTxtSeConfigurado();
        return dtoAtualizado;
    }

    public void inativar(Long musicaId) {
        Musicas musica = repository.findById(musicaId)
                .orElseThrow(() -> new EntityNotFoundException(STR."Música não encontrada com ID: \{musicaId}"));
        musica.setAtivo(false);
        repository.save(musica);

        sincronizarTxtSeConfigurado();
    }
}