package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Frota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FrotaRepository extends JpaRepository<Frota, Long> {

    @Query(value = "SELECT AVG(km_andado / quantidade_litros) " +
            "FROM frota " +
            "WHERE MONTH(data_abastecimento) = :mes " +
            "AND YEAR(data_abastecimento) = :ano",
            nativeQuery = true)
    Double encontrarMediaConsumoPorMes(@Param("mes") int mes,
                                       @Param("ano") int ano);
}