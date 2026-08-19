package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.VendedorDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Vendedor;
import br.com.drs.radiotv_app_pro.service.escritorio.VendedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{chaveUsuario}")
    public List<Vendedor> buscarPoirChaveUsuario(@PathVariable String chaveUsuario) {
        return service.buscarPorChaveUsuario(chaveUsuario);
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