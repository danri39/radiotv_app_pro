package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Familia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamiliaRepository extends JpaRepository<Familia, Long> {

    List<Familia> findByChaveUsuario(String chaveUsuario);
}
