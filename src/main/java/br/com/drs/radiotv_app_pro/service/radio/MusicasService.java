package br.com.drs.radiotv_app_pro.service.radio;

import br.com.drs.radiotv_app_pro.dto.radio.MusicasDTO;
import br.com.drs.radiotv_app_pro.mapper.radio.MusicasMapper;
import br.com.drs.radiotv_app_pro.model.radio.Musicas;
import br.com.drs.radiotv_app_pro.repository.radio.MusicasRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicasService {

    private final MusicasRepository repository;
    private final MusicasMapper mapper;

    public MusicasDTO salvar(MusicasDTO dto) {
        Musicas musicaNova = mapper.toEntity(dto);
        musicaNova = repository.save(musicaNova);
        return mapper.toDto(musicaNova);
    }

    public List<MusicasDTO> listar() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<MusicasDTO> buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    public Optional<MusicasDTO> buscarPorNome(String nome) {
        return repository.findByNomeMusica(nome)
                .map(mapper::toDto);
    }

    public Optional<MusicasDTO> buscarPorArtista(String artista) {
        return repository.findByArtista(artista)
                .map(mapper::toDto);
    }

    public MusicasDTO atualizar(Long id, MusicasDTO dto) {
        Musicas musicaExistente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Música não encontrada com ID: " + id));

        mapper.updateEntityFromDto(dto, musicaExistente);
        Musicas atualizada = repository.save(musicaExistente);
        return mapper.toDto(atualizada);
    }

    public void inativar(Long musicaId) {
        Musicas musica = repository.findById(musicaId)
                .orElseThrow(() -> new EntityNotFoundException("Música não encontrada com ID: " + musicaId));
        musica.setAtivo(false);
        repository.save(musica);
    }
}