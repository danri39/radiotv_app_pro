package br.com.drs.radiotv_app_pro.repository;

import br.com.drs.radiotv_app_pro.model.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendedorRepository extends JpaRepository<Vendedor,Long> {
}
