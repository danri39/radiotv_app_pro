package br.com.drs.radiotv_app_pro.repository.escritorio;


import br.com.drs.radiotv_app_pro.model.escritorio.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    // 1. Busca contratos com parcelas em aberto (não pagas)
    @Query("SELECT DISTINCT c FROM Contrato c JOIN FETCH c.pagamentos p WHERE p.paga = false AND c.ativo = true")
    List<Contrato> findContratosComParcelasEmAberto();

    // 2. Busca contratos próximos do fim (ex: faltam X dias) e que tenham alguma parcela NÃO faturada
    @Query("SELECT DISTINCT c FROM Contrato c JOIN FETCH c.pagamentos p " +
            "WHERE c.dataFinal BETWEEN :hoje AND :dataLimite " +
            "AND p.faturado = false AND c.ativo = true")
    List<Contrato> findContratosProximosVencimentoNaoFaturados(
            @Param("hoje") LocalDate hoje,
            @Param("dataLimite") LocalDate dataLimite
    );

    // 3. Busca contratos que possuem parcelas com data de pagamento menor que HOJE e não estão pagas (Inadimplência)
    @Query("SELECT DISTINCT c FROM Contrato c JOIN FETCH c.pagamentos p " +
            "WHERE p.dataPagamento < :hoje AND p.paga = false AND c.ativo = true")
    List<Contrato> findContratosInadimplentes(@Param("hoje") LocalDate hoje);

    @Query("SELECT DISTINCT c FROM Contrato c " +
            "LEFT JOIN FETCH c.cliente " +
            "LEFT JOIN FETCH c.agencia")
    List<Contrato> findAllComRelacionamentos();
}