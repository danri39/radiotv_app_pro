package br.com.drs.radiotv_app_pro.repository;

import br.com.drs.radiotv_app_pro.model.Programa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramaRepository extends JpaRepository<Programa, Long> {
}
