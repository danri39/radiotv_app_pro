package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Ponto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PontoRepository extends JpaRepository<Ponto, Long> {

    List<Ponto> findByFuncionarioIdAndHoraEntradaBetween(Long funcionarioId, LocalDateTime inicio, LocalDateTime fim);
}
