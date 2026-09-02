package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Ponto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PontoRepository extends JpaRepository<Ponto, Long> {

    Optional<Ponto> findFirstByChaveUsuarioAndHoraEntradaBetween(String chaveUsuario, LocalDateTime inicio, LocalDateTime fim);

    List<Ponto> findByChaveUsuarioAndEnviadoParaCorrecaoTrue(String chaveUsuario);

    List<Ponto> findByPossuiInconsistenciaTrue();
}