package br.com.drs.radiotv_app_pro.repository;

import br.com.drs.radiotv_app_pro.model.Filho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilhoRepository extends JpaRepository<Filho, Long> {

    List<Filho> findByFuncionarioId(Long funcionarioId);
}
