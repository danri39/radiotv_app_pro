package br.com.drs.radiotv_app_pro.repository.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.FuncionarioBeneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncionarioBeneficioRepository extends JpaRepository<FuncionarioBeneficio, Long> {
    List<FuncionarioBeneficio> findByFuncionarioId(Long funcionarioId);
    List<FuncionarioBeneficio> findByFuncionarioIdAndAtivoTrue(Long funcionarioId);
}