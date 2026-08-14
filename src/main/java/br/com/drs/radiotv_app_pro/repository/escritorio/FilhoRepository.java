package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Filho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilhoRepository extends JpaRepository<Filho, Long> {

    List<Filho> findByFuncionarioId(Long funcionarioId);
}
