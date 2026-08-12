package br.com.drs.radiotv_app_pro.controller;

import br.com.drs.radiotv_app_pro.dto.VendedorDTO;
import br.com.drs.radiotv_app_pro.model.Vendedor;
import br.com.drs.radiotv_app_pro.service.VendedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/vendedor")
@RequiredArgsConstructor
public class VendedorController {

    private final VendedorService service;

    @PostMapping
    public ResponseEntity<VendedorDTO> salvar(@RequestBody VendedorDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @GetMapping
    public List<Vendedor> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Vendedor> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendedorDTO> atualizar(@RequestBody VendedorDTO dto, @PathVariable Long id) {
        VendedorDTO vendedorAtualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(vendedorAtualizado);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.deletar(id);
    }
}