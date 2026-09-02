package br.com.drs.radiotv_app_pro.service.radio;

import br.com.drs.radiotv_app_pro.dto.radio.MusicasDTO;
import br.com.drs.radiotv_app_pro.mapper.radio.MusicasMapper;
import br.com.drs.radiotv_app_pro.model.radio.Musicas;
import br.com.drs.radiotv_app_pro.repository.radio.MusicasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MusicasService {

    private final MusicasRepository repository;
    private final MusicasMapper musicasMapper;

    public List<MusicasDTO> listarTodas() {
        return musicasMapper.toDtoList(repository.findAll());
    }

    public MusicasDTO buscarPorId(Long id) {
        Musicas musica = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Música não encontrada: " + id));
        return musicasMapper.toDto(musica);
    }

    public MusicasDTO criar(MusicasDTO dto) {
        Musicas musica = musicasMapper.toEntity(dto);
        musica = repository.save(musica);
        return musicasMapper.toDto(musica);
    }

    public MusicasDTO atualizar(Long id, MusicasDTO dto) {
        Musicas musicaExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Música não encontrada: " + id));

        musicasMapper.updateEntityFromDto(dto, musicaExistente);
        musicaExistente = repository.save(musicaExistente);

        return musicasMapper.toDto(musicaExistente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}