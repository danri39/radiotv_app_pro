package br.com.drs.radiotv_app_pro.repository.radio;

import br.com.drs.radiotv_app_pro.model.radio.RegistroMusical;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistroMusicalRepository extends JpaRepository<RegistroMusical, String> {
    Optional<RegistroMusical> findByIdAndAtivoTrue(String id);
}
