package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.FrotaDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.FrotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/frota")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FrotaController {

    private final FrotaService frotaService;

    // CREATE
    @PostMapping
    public ResponseEntity<FrotaDTO> criar(@RequestBody FrotaDTO frotaDTO) {
        try {
            FrotaDTO frotaSalva = frotaService.salvar(frotaDTO);
            return ResponseEntity.ok(frotaSalva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // READ - Todos
    @GetMapping
    public ResponseEntity<List<FrotaDTO>> listarTodos() {
        try {
            List<FrotaDTO> frotas = frotaService.listarTodos();
            return ResponseEntity.ok(frotas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // READ - Por ID
    @GetMapping("/{id}")
    public ResponseEntity<FrotaDTO> buscarPorId(@PathVariable Long id) {
        try {
            FrotaDTO frota = frotaService.buscarPorId(id);
            return ResponseEntity.ok(frota);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<FrotaDTO> atualizar(@PathVariable Long id, @RequestBody FrotaDTO frotaDTO) {
        try {
            FrotaDTO frotaAtualizada = frotaService.atualizar(id, frotaDTO);
            return ResponseEntity.ok(frotaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable Long id) {
        try {
            frotaService.deletar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Registro excluído com sucesso");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // CÁLCULO - Média de consumo por mês
    @GetMapping("/consumo/media")
    public ResponseEntity<Map<String, Object>> mediaConsumoPorMes(
            @RequestParam int mes,
            @RequestParam int ano) {
        try {
            Double media = frotaService.calcularMediaConsumoPorMes(mes, ano);

            Map<String, Object> response = new HashMap<>();
            response.put("mes", mes);
            response.put("ano", ano);
            response.put("mediaConsumoKmPorLitro", media);
            response.put("unidade", "km/l");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // CÁLCULO - Consumo de um registro específico
    @GetMapping("/{id}/consumo")
    public ResponseEntity<? extends Object> consumoRegistro(@PathVariable Long id) {
        try {
            Double consumo = frotaService.calcularConsumoRegistro(id);

            if (consumo == null) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Não foi possível calcular o consumo. Verifique se kmAndado e quantidadeLitros estão preenchidos.");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("id", id);
            response.put("consumoKmPorLitro", consumo);
            response.put("unidade", "km/l");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // LISTAR - Por mês/ano
    @GetMapping("/filtro/mes-ano")
    public ResponseEntity<List<FrotaDTO>> listarPorMesAno(
            @RequestParam int mes,
            @RequestParam int ano) {
        try {
            List<FrotaDTO> frotas = frotaService.listarPorMesAno(mes, ano);
            return ResponseEntity.ok(frotas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}