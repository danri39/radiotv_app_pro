package br.com.drs.radiotv_app_pro.repository;

import br.com.drs.radiotv_app_pro.model.Ponto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PontoRepository extends JpaRepository<Ponto, Long> {

    List<Ponto> findByFuncionarioIdAndHoraEntradaBetween(Long funcionarioId, LocalDateTime inicio, LocalDateTime fim);
}
