package br.com.drs.radiotv_app_pro.repository;

import br.com.drs.radiotv_app_pro.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findByDescricao(String descricao);
}
