package br.com.drs.radiotv_app_pro.repository.escritorio;


import br.com.drs.radiotv_app_pro.model.escritorio.FolhaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolhaPagamentoRepository extends JpaRepository<FolhaPagamento, Long> {

    // Evita que a folha do mesmo mês seja criada duas vezes para o mesmo funcionário
    Optional<FolhaPagamento> findByFuncionarioIdAndMesAno(Long funcionarioId, String mesAno);

    // Histórico de folhas de um profissional específico
    List<FolhaPagamento> findByFuncionarioId(Long funcionarioId);
}
