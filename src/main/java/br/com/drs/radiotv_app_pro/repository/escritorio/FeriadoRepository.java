package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Feriado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeriadoRepository extends JpaRepository<Feriado, Long> {
}
