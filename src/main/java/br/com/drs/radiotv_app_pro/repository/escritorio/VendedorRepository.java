package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    Optional<Vendedor> findByChaveUsuario(String chaveUsuario);
}
