package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.DiasSemana;
import br.com.drs.radiotv_app_pro.model.escritorio.Programa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProgramaRepository extends JpaRepository<Programa, Long> {

    @Query("SELECT DISTINCT p FROM Programa p LEFT JOIN FETCH p.diasSemana ds " +
            "WHERE p.ativo = true AND (:ehFeriado = true AND p.feriados = true " +
            "OR :ehFeriado = false AND ds IN :diaSemana)")
    List<Programa> buscarProgramasPorDiaOuFeriado(@Param("diaSemana") DiasSemana diaSemana,
                                                  @Param("ehFeriado") boolean ehFeriado);
}