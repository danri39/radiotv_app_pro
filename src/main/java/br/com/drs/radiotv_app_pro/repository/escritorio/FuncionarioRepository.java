package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);

    Optional<Object> findByChaveUsuario(String chaveUsuario);

    Optional<Funcionario> findByNomeAndChaveUsuario(String nome, String chaveUsuario);
}
