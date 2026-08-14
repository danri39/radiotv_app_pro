package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Ferias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeriasRepository extends JpaRepository<Ferias, Long> {
    List<Ferias> findByFuncionarioId(Long funcionarioId);
}