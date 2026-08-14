package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Agencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgenciaRepository extends JpaRepository<Agencia, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);

    Optional<Agencia> findByCnpj(String finalCnpjAgencia);
}