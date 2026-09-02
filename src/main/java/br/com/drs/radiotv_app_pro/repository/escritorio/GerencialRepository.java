package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GerencialRepository extends JpaRepository<Funcionario, Long> {

    // Total de vendedores com atividade no mês ou ativos no sistema
    @Query(value = """
        SELECT COUNT(DISTINCT c.chave_usuario) 
        FROM comissao c 
        WHERE MONTH(c.data_pagamento_real) = :mes 
          AND YEAR(c.data_pagamento_real) = :ano
    """, nativeQuery = true)
    Integer countVendedoresAtivosMes(@Param("mes") int mes, @Param("ano") int ano);

    // Total faturado/recebido no mês através das parcelas dos contratos
    @Query(value = """
        SELECT COALESCE(SUM(c.valor_parcela), 0)
        FROM comissao c
        WHERE MONTH(c.data_pagamento_real) = :mes 
          AND YEAR(c.data_pagamento_real) = :ano
    """, nativeQuery = true)
    BigDecimal somarFaturamentoMes(@Param("mes") int mes, @Param("ano") int ano);

    // Quantidade de contratos/parcelas fechadas no mês
    @Query(value = """
        SELECT COUNT(DISTINCT c.numero_fatura)
        FROM comissao c
        WHERE MONTH(c.data_pagamento_real) = :mes 
          AND YEAR(c.data_pagamento_real) = :ano
    """, nativeQuery = true)
    Integer countContratosFechadosMes(@Param("mes") int mes, @Param("ano") int ano);

    // Total de comissões liquidadas no mês
    @Query(value = """
        SELECT COALESCE(SUM(c.valor_comissao_vendedor), 0)
        FROM comissao c
        WHERE MONTH(c.data_pagamento_real) = :mes 
          AND YEAR(c.data_pagamento_real) = :ano
          AND (c.paga_vendedor = 1 OR c.paga_vendedor = true)
    """, nativeQuery = true)
    BigDecimal somarComissoesLiquidadasMes(@Param("mes") int mes, @Param("ano") int ano);

    // Total de comissões pendentes (agência ou vendedor a liquidar)
    @Query(value = """
        SELECT COALESCE(SUM(c.valor_comissao_agencia), 0)
        FROM comissao c
        WHERE (c.paga_agencia = 0 OR c.paga_agencia = false OR c.paga_agencia IS NULL)
    """, nativeQuery = true)
    BigDecimal somarComissoesPendentes();

    // Destaque de Vendas: Vendedor que mais gerou faturamento em parcelas no mês
    @Query(value = """
        SELECT u.nome, COALESCE(SUM(c.valor_parcela), 0) as total_vendido
        FROM comissao c
        JOIN usuario u ON u.chave_usuario = c.chave_usuario
        WHERE MONTH(c.data_pagamento_real) = :mes 
          AND YEAR(c.data_pagamento_real) = :ano
        GROUP BY u.nome
        ORDER BY total_vendido DESC
        LIMIT 1
    """, nativeQuery = true)
    List<Object[]> findMelhorVendedorMes(@Param("mes") int mes, @Param("ano") int ano);
}