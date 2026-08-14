package br.com.drs.radiotv_app_pro.repository;

import br.com.drs.radiotv_app_pro.model.Compras;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComprasRepository extends JpaRepository<Compras, Long> {
}