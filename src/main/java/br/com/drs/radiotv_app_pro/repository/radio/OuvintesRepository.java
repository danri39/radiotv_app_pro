package br.com.drs.radiotv_app_pro.repository.radio;

import br.com.drs.radiotv_app_pro.model.radio.Ouvintes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OuvintesRepository extends JpaRepository<Ouvintes, Long> {

    Optional<Ouvintes> findByNome(String nome);
}
