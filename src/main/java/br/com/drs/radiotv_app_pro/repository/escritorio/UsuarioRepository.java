package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    Optional<Usuario> findByChaveUsuario(String chaveUsuario);

    Optional<Usuario> findByChavePrimeiroAcesso(String chavePrimeiroAcesso);

    Optional<Usuario> findByChaveTrocaSenha(String chaveTrocaSenha);

    Optional<Object> findByEmail(String email);

    @Query("SELECT u.nome AS nome, u.papeis AS papel FROM Usuario u WHERE u.nome = :nome")
    Optional<Usuario.UsuarioResumoProjection> encontrarNomeEPapelPorNome(@Param("nome") String nome);
}