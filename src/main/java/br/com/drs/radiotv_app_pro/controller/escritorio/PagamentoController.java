package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.PagamentoDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Pagamento;
import br.com.drs.radiotv_app_pro.service.escritorio.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pagamento")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService service;

    @PostMapping
    public ResponseEntity<PagamentoDTO> save(@RequestBody PagamentoDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @GetMapping
    public List<Pagamento> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Optional<Pagamento> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDTO> atualizar(@PathVariable Long id, @RequestBody PagamentoDTO dto) {
        PagamentoDTO atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}