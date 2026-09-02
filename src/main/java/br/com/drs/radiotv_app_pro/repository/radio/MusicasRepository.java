package br.com.drs.radiotv_app_pro.repository.radio;

import br.com.drs.radiotv_app_pro.model.radio.Musicas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MusicasRepository extends JpaRepository<Musicas, Long> {
   
    Optional<Musicas> findByArtista(String artista);

    Optional<Musicas> findByNomeMusica(String nomeMusica);

    Optional<Musicas> findByArtistaAndNomeMusica(String artista, String nomeMusica);
}
