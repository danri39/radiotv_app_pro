package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FrotaDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Frota;
import br.com.drs.radiotv_app_pro.model.escritorio.Veiculos;
import br.com.drs.radiotv_app_pro.repository.escritorio.FrotaRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.VeiculosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FrotaService {

    private final FrotaRepository frotaRepository;

    private final VeiculosRepository veiculosRepository;

    // CREATE
    public FrotaDTO salvar(FrotaDTO frotaDTO) {
        Frota frota = converterParaEntity(frotaDTO);

        // Calcula kmAndado se não foi informado
        if (frota.getKmAndado() == null && frota.getKmSaida() != null && frota.getKmChegada() != null) {
            frota.setKmAndado(frota.getKmChegada() - frota.getKmSaida());
        }

        // Valida quantidade de litros
        if (frota.getQuantidadeLitros() == null || frota.getQuantidadeLitros() <= 0) {
            throw new IllegalArgumentException("Quantidade de litros deve ser maior que zero");
        }

        Frota frotaSalva = frotaRepository.save(frota);
        return converterParaDTO(frotaSalva);
    }

    // READ - Todos
    public List<FrotaDTO> listarTodos() {
        List<Frota> frotas = frotaRepository.findAll();
        return frotas.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // READ - Por ID
    public FrotaDTO buscarPorId(Long id) {
        Frota frota = frotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de frota não encontrado com ID: " + id));
        return converterParaDTO(frota);
    }

    // UPDATE
    public FrotaDTO atualizar(Long id, FrotaDTO frotaDTO) {
        Frota frotaExistente = frotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de frota não encontrado com ID: " + id));

        // Atualiza os campos
        atualizarDados(frotaExistente, frotaDTO);

        // Recalcula kmAndado se necessário
        if (frotaExistente.getKmAndado() == null &&
                frotaExistente.getKmSaida() != null &&
                frotaExistente.getKmChegada() != null) {
            frotaExistente.setKmAndado(frotaExistente.getKmChegada() - frotaExistente.getKmSaida());
        }

        Frota frotaAtualizada = frotaRepository.save(frotaExistente);
        return converterParaDTO(frotaAtualizada);
    }

    // DELETE
    public void deletar(Long id) {
        if (!frotaRepository.existsById(id)) {
            throw new RuntimeException("Registro de frota não encontrado com ID: " + id);
        }
        frotaRepository.deleteById(id);
    }

    // CÁLCULO - Média de consumo por mês
    public Double calcularMediaConsumoPorMes(int mes, int ano) {
        Double media = frotaRepository.encontrarMediaConsumoPorMes(mes, ano);
        return media != null ? Math.round(media * 100.0) / 100.0 : 0.0;
    }

    // CÁLCULO - Consumo de um registro específico
    public Double calcularConsumoRegistro(Long id) {
        Frota frota = frotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de frota não encontrado com ID: " + id));

        if (frota.getKmAndado() == null || frota.getQuantidadeLitros() == null || frota.getQuantidadeLitros() <= 0) {
            return null;
        }

        double consumo = frota.getKmAndado() / frota.getQuantidadeLitros();
        return Math.round(consumo * 100.0) / 100.0;
    }

    // LISTAR - Por mês/ano
    public List<FrotaDTO> listarPorMesAno(int mes, int ano) {
        LocalDate inicioMes = LocalDate.of(ano, mes, 1);
        LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        // Você pode adicionar este método no repository se precisar
        // Por enquanto, filtramos em memória
        return listarTodos().stream()
                .filter(dto -> dto.getDataAbastecimento() != null
                        && dto.getDataAbastecimento().getMonthValue() == mes
                        && dto.getDataAbastecimento().getYear() == ano)
                .collect(Collectors.toList());
    }

    // MÉTODOS AUXILIARES

    private Frota converterParaEntity(FrotaDTO dto) {
        Frota frota = new Frota();

        frota.setId(dto.getId());
        frota.setChaveUsuario(dto.getChaveUsuario());
        frota.setSaida(dto.getSaida());
        frota.setChegada(dto.getChegada());
        frota.setKmSaida(dto.getKmSaida());
        frota.setKmChegada(dto.getKmChegada());
        frota.setKmAndado(dto.getKmAndado());
        frota.setOcorrenciaSaida(dto.getOcorrenciaSaida());
        frota.setOcorrenciaChegada(dto.getOcorrenciaChegada());
        frota.setUltimaRevisao(dto.getUltimaRevisao());
        frota.setProximaRevisao(dto.getProximaRevisao());
        frota.setDataAbastecimento(dto.getDataAbastecimento());
        frota.setQuantidadeLitros(dto.getQuantidadeLitros());

        if (dto.getVeiculosId() != null) {
            Veiculos veiculo = veiculosRepository.findById(dto.getVeiculosId())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + dto.getVeiculosId()));
            frota.setVeiculos(veiculo);
        }

        return frota;
    }

    private FrotaDTO converterParaDTO(Frota frota) {
        FrotaDTO dto = new FrotaDTO();

        dto.setId(frota.getId());
        dto.setChaveUsuario(frota.getChaveUsuario());
        dto.setSaida(frota.getSaida());
        dto.setChegada(frota.getChegada());
        dto.setKmSaida(frota.getKmSaida());
        dto.setKmChegada(frota.getKmChegada());
        dto.setKmAndado(frota.getKmAndado());
        dto.setOcorrenciaSaida(frota.getOcorrenciaSaida());
        dto.setOcorrenciaChegada(frota.getOcorrenciaChegada());
        dto.setUltimaRevisao(frota.getUltimaRevisao());
        dto.setProximaRevisao(frota.getProximaRevisao());
        dto.setDataAbastecimento(frota.getDataAbastecimento());
        dto.setQuantidadeLitros(frota.getQuantidadeLitros());

        if (frota.getVeiculos() != null) {
            dto.setVeiculosId(frota.getVeiculos().getId());
        }

        // Calcula consumo
        if (frota.getKmAndado() != null && frota.getQuantidadeLitros() != null && frota.getQuantidadeLitros() > 0) {
            double consumo = frota.getKmAndado() / frota.getQuantidadeLitros();
            dto.setConsumoKmPorLitro(Math.round(consumo * 100.0) / 100.0);
        }

        return dto;
    }

    private void atualizarDados(Frota frotaExistente, FrotaDTO frotaDTO) {
        if (frotaDTO.getChaveUsuario() != null) {
            frotaExistente.setChaveUsuario(frotaDTO.getChaveUsuario());
        }
        if (frotaDTO.getSaida() != null) {
            frotaExistente.setSaida(frotaDTO.getSaida());
        }
        if (frotaDTO.getChegada() != null) {
            frotaExistente.setChegada(frotaDTO.getChegada());
        }
        if (frotaDTO.getKmSaida() != null) {
            frotaExistente.setKmSaida(frotaDTO.getKmSaida());
        }
        if (frotaDTO.getKmChegada() != null) {
            frotaExistente.setKmChegada(frotaDTO.getKmChegada());
        }
        if (frotaDTO.getKmAndado() != null) {
            frotaExistente.setKmAndado(frotaDTO.getKmAndado());
        }
        if (frotaDTO.getOcorrenciaSaida() != null) {
            frotaExistente.setOcorrenciaSaida(frotaDTO.getOcorrenciaSaida());
        }
        if (frotaDTO.getOcorrenciaChegada() != null) {
            frotaExistente.setOcorrenciaChegada(frotaDTO.getOcorrenciaChegada());
        }
        if (frotaDTO.getUltimaRevisao() != null) {
            frotaExistente.setUltimaRevisao(frotaDTO.getUltimaRevisao());
        }
        if (frotaDTO.getProximaRevisao() != null) {
            frotaExistente.setProximaRevisao(frotaDTO.getProximaRevisao());
        }
        if (frotaDTO.getDataAbastecimento() != null) {
            frotaExistente.setDataAbastecimento(frotaDTO.getDataAbastecimento());
        }
        if (frotaDTO.getQuantidadeLitros() != null) {
            frotaExistente.setQuantidadeLitros(frotaDTO.getQuantidadeLitros());
        }
        if (frotaDTO.getVeiculosId() != null) {
            Veiculos veiculo = veiculosRepository.findById(frotaDTO.getVeiculosId())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + frotaDTO.getVeiculosId()));
            frotaExistente.setVeiculos(veiculo);
        }
    }
}