package br.com.drs.radiotv_app_pro.repository.radio;

import br.com.drs.radiotv_app_pro.model.radio.RegistroComercial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistroComercialRepository extends JpaRepository<RegistroComercial, String> {

    Optional<RegistroComercial> findByIdAndAtivoTrue(String id);
}
