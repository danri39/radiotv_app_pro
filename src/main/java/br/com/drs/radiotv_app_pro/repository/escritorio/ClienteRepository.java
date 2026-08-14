package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {


    boolean existsByCpf(String cpf);

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);

    Optional<Cliente> findByCnpjCpf(String finalCnpjCpfCliente);

    @Query("SELECT c FROM Cliente c WHERE c.cnpj = :documento OR c.cpf = :documento")
    Optional<Cliente> findByCnpjOrCpf(@Param("documento") String documento);
}
