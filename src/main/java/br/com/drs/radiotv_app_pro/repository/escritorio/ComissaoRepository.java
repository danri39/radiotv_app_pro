package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Comissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ComissaoRepository extends JpaRepository<Comissao, Long> {

    // Lista todas as comissões de um vendedor
    List<Comissao> findByChaveUsuario(String chaveUsuario);

    // Soma comissões a pagar para o vendedor em um período que ainda NÃO foram pagas na folha
    @Query("SELECT COALESCE(SUM(c.valorComissaoVendedor), 0) FROM Comissao c " +
            "WHERE c.chaveUsuario = :chaveUsuario " +
            "AND c.pagaVendedor = false " +
            "AND c.dataPagamentoReal BETWEEN :dataInicio AND :dataFim")
    BigDecimal somarComissoesPendentesVendedorNoPeriodo(
            @Param("chaveUsuario") String chaveUsuario,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    // Soma comissões liberadas de uma agência que ainda não foram pagas
    @Query("SELECT COALESCE(SUM(c.valorComissaoAgencia), 0) FROM Comissao c " +
            "WHERE c.agencia.id = :agenciaId " +
            "AND c.pagaAgencia = false")
    BigDecimal somarComissoesPendentesAgencia(@Param("agenciaId") Long agenciaId);
}