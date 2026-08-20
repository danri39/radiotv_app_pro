package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FolhaPagamentoDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.FolhaPagamentoMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.FolhaPagamento;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.model.escritorio.FuncionarioBeneficio;
import br.com.drs.radiotv_app_pro.repository.escritorio.FolhaPagamentoRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioBeneficioRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FolhaPagamentoService {

    private final FolhaPagamentoRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final FuncionarioBeneficioRepository funcionarioBeneficioRepository;
    private final FolhaPagamentoMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public FolhaPagamentoDTO calcularFolha(FolhaPagamentoDTO dto) {
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new IllegalArgumentException(STR."Funcionário não localizado com ID: \{dto.getFuncionarioId()}"));

        if (funcionario.getSalario() == null || funcionario.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O funcionário selecionado não possui um salário base válido cadastrado.");
        }

        Optional<FolhaPagamento> folhaExistente = repository.findByFuncionarioIdAndMesAno(dto.getFuncionarioId(), dto.getMesAno());

        FolhaPagamento folha = folhaExistente.orElseGet(() -> FolhaPagamento.builder()
                .funcionario(funcionario)
                .mesAno(dto.getMesAno())
                .build());

        preencherDadosCalculo(folha, funcionario, dto.getMesAno());

        FolhaPagamento salva = repository.save(folha);
        exportarArquivoSalariosTxt(dto.getMesAno());
        return mapper.toDTO(salva);
    }

    @Transactional
    public List<FolhaPagamentoDTO> processarFolhaLote(String mesAno) {
        List<Funcionario> funcionariosAtivos = funcionarioRepository.findAll().stream()
                .filter(f -> f.getAtivo() != null && f.getAtivo() && f.getSalario() != null && f.getSalario().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        List<FolhaPagamentoDTO> folhasProcessadas = new ArrayList<>();

        for (Funcionario f : funcionariosAtivos) {
            Optional<FolhaPagamento> folhaExistente = repository.findByFuncionarioIdAndMesAno(f.getId(), mesAno);
            FolhaPagamento folha = folhaExistente.orElseGet(() -> FolhaPagamento.builder()
                    .funcionario(f)
                    .mesAno(mesAno)
                    .build());

            preencherDadosCalculo(folha, f, mesAno);
            folhasProcessadas.add(mapper.toDTO(repository.save(folha)));
        }

        exportarArquivoSalariosTxt(mesAno);
        return folhasProcessadas;
    }

    private void preencherDadosCalculo(FolhaPagamento folha, Funcionario funcionario, String mesAno) {
        BigDecimal salarioBase = funcionario.getSalario();
        BigDecimal comissao = buscarComissaoAutomatica(funcionario, mesAno);

        // Remuneração Bruta Total = Salário Base + Comissões
        BigDecimal totalBruto = salarioBase.add(comissao);

        // Tributos calculados sobre a Remuneração Bruta
        BigDecimal inss = calcularInss(totalBruto);
        BigDecimal irrf = calcularIrrf(totalBruto.subtract(inss));

        // Dedução dos Benefícios Ativos do Funcionário
        List<FuncionarioBeneficio> beneficiosAtivos = funcionarioBeneficioRepository.findByFuncionarioIdAndAtivoTrue(funcionario.getId());
        BigDecimal descontosBeneficios = beneficiosAtivos.stream()
                .map(fb -> fb.getValorTotalDesconto() != null ? fb.getValorTotalDesconto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescontos = inss.add(irrf).add(descontosBeneficios);
        BigDecimal liquido = totalBruto.subtract(totalDescontos);

        folha.setSalarioBruto(totalBruto);
        folha.setValorComissao(comissao);
        folha.setDescontoInss(inss);
        folha.setDescontoIrrf(irrf);
        folha.setDescontoBeneficios(descontosBeneficios);
        folha.setTotalDescontos(totalDescontos);
        folha.setSalarioLiquido(liquido);
        folha.setFechada(true);
    }

    private BigDecimal buscarComissaoAutomatica(Funcionario funcionario, String mesAno) {
        if (funcionario.getChaveUsuario() == null || funcionario.getChaveUsuario().isBlank()) {
            return BigDecimal.ZERO;
        }

        try {
            String[] partes = mesAno.split("/");
            int mes = Integer.parseInt(partes[0]);
            int ano = Integer.parseInt(partes[1]);

            // Consulta direta na tabela comissao
            String sql = """
                SELECT COALESCE(SUM(valor_comissao_vendedor), 0)
                FROM comissao
                WHERE chave_usuario = :chave
                  AND MONTH(data_pagamento_real) = :mes
                  AND YEAR(data_pagamento_real) = :ano
                  AND (paga_vendedor = 1 OR paga_vendedor = true)
            """;

            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("chave", funcionario.getChaveUsuario());
            query.setParameter("mes", mes);
            query.setParameter("ano", ano);

            Object resultado = query.getSingleResult();
            if (resultado instanceof Number num) {
                return BigDecimal.valueOf(num.doubleValue()).setScale(2, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println(STR."Erro ao buscar comissões da tabela comissao: \{e.getMessage()}");
            return BigDecimal.ZERO;
        }
    }

    public void exportarArquivoSalariosTxt(String mesAno) {
        List<FolhaPagamento> folhas = repository.findAll().stream()
                .filter(f -> f.getMesAno().equals(mesAno))
                .toList();

        if (folhas.isEmpty()) return;

        try {
            File pasta = new File("C:\\RadioTV\\Arquivos");
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            File arquivo = new File(pasta, "salarios.txt");
            try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo, false))) {
                writer.println("=========================================================================================================");
                writer.println("RELAÇÃO DE PAGAMENTO DE SALÁRIOS - RADIOTV APP PRO");
                writer.println(STR."COMPETÊNCIA: \{mesAno} | GERADO EM: \{LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))}");
                writer.println("=========================================================================================================");
                writer.printf("%-25s | %-14s | %-12s | %-8s | %-12s | %-15s%n", "FUNCIONÁRIO", "CPF", "COMISSÃO", "AGÊNCIA", "CONTA", "VALOR LÍQUIDO");
                writer.println("---------------------------------------------------------------------------------------------------------");

                BigDecimal totalLiquidoGeral = BigDecimal.ZERO;

                for (FolhaPagamento f : folhas) {
                    Funcionario func = f.getFuncionario();
                    writer.printf("%-25s | %-14s | R$ %9.2f | %-8s | %-12s | R$ %12.2f%n",
                            func.getNome(),
                            func.getCpf() != null ? func.getCpf() : "N/I",
                            f.getValorComissao() != null ? f.getValorComissao() : BigDecimal.ZERO,
                            func.getAgencia() != null ? func.getAgencia() : "N/I",
                            func.getConta() != null ? func.getConta() : "N/I",
                            f.getSalarioLiquido()
                    );
                    totalLiquidoGeral = totalLiquidoGeral.add(f.getSalarioLiquido());
                }

                writer.println("=========================================================================================================");
                writer.printf("TOTAL GERAL DA FOLHA: R$ %.2f%n", totalLiquidoGeral);
                writer.println("=========================================================================================================");
            }
        } catch (Exception e) {
            System.err.println(STR."Erro ao gerar arquivo salarios.txt: \{e.getMessage()}");
        }
    }

    public List<FolhaPagamentoDTO> listarTodas() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    public List<FolhaPagamentoDTO> listarPorFuncionario(Long funcionarioId) {
        return repository.findByFuncionarioId(funcionarioId).stream().map(mapper::toDTO).toList();
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Folha de pagamento não encontrada.");
        }
        repository.deleteById(id);
    }

    private BigDecimal calcularInss(BigDecimal salario) {
        double sal = salario.doubleValue();
        double desconto = 0;

        if (sal <= 1518.00) {
            desconto = sal * 0.075;
        } else if (sal <= 2793.88) {
            desconto = (1518.00 * 0.075) + ((sal - 1518.00) * 0.09);
        } else if (sal <= 4190.83) {
            desconto = (1518.00 * 0.075) + ((2793.88 - 1518.00) * 0.09) + ((sal - 2793.88) * 0.12);
        } else if (sal <= 7786.02) {
            desconto = (1518.00 * 0.075) + ((2793.88 - 1518.00) * 0.09) + ((4190.83 - 2793.88) * 0.12) + ((sal - 4190.83) * 0.14);
        } else {
            desconto = 908.85;
        }
        return BigDecimal.valueOf(desconto).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularIrrf(BigDecimal baseCalculo) {
        double base = baseCalculo.doubleValue();
        double imposto = 0;

        if (base <= 2259.20) {
            imposto = 0;
        } else if (base <= 2826.65) {
            imposto = (base * 0.075) - 169.44;
        } else if (base <= 3751.05) {
            imposto = (base * 0.15) - 381.44;
        } else if (base <= 4664.68) {
            imposto = (base * 0.225) - 662.77;
        } else {
            imposto = (base * 0.275) - 896.00;
        }

        if (imposto < 0) imposto = 0;
        return BigDecimal.valueOf(imposto).setScale(2, RoundingMode.HALF_UP);
    }
}