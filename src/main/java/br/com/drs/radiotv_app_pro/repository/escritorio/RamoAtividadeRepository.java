package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.RamoAtividade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RamoAtividadeRepository extends JpaRepository<RamoAtividade, Long> {
    Optional<RamoAtividade> findByDescricao(String descricao);
    Boolean existsByDescricaoIgnoreCase(String descricao);
}