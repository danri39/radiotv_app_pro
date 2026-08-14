package br.com.drs.radiotv_app_pro.repository;

import br.com.drs.radiotv_app_pro.model.Beneficios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiosRepository extends JpaRepository<Beneficios, Long> {

    List<Beneficios> findByFuncionarioId(Long funcionarioId);
}
