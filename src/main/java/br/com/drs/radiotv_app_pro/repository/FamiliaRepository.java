package br.com.drs.radiotv_app_pro.repository;

import br.com.drs.radiotv_app_pro.model.Familia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamiliaRepository extends JpaRepository<Familia, Long> {

    List<Familia> findByFuncionarioId(Long funcionarioId);
}
