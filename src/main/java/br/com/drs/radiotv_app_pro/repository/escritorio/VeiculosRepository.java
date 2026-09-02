package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Veiculos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VeiculosRepository extends JpaRepository<Veiculos, Long> {
    Optional<Veiculos> findByPlaca(String placa);
}
