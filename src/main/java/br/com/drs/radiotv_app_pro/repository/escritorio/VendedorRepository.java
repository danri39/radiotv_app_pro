package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VendedorRepository extends JpaRepository<Vendedor, Long> {
    @Query("SELECT DISTINCT v FROM Vendedor v JOIN FETCH v.funcionario")
    List<Vendedor> findAllComFuncionario();
}
