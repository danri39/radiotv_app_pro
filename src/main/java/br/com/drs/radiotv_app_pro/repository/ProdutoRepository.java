package br.com.drs.radiotv_app_pro.repository;

import br.com.drs.radiotv_app_pro.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}