package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.VeiculosDTO;
import br.com.drs.radiotv_app_pro.model.escritorio.Veiculos;
import br.com.drs.radiotv_app_pro.service.escritorio.VeiculosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/veiculos")
@RequiredArgsConstructor
public class VeiculosController {

    private final VeiculosService service;

    @PostMapping
    public ResponseEntity<VeiculosDTO> save(@RequestBody VeiculosDTO dto){
        return ResponseEntity.ok(service.salvar(dto));
    }

    @GetMapping
    public List<Veiculos> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Veiculos> buscar(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    @GetMapping("/veiculos/{placa}")
    public Optional<Veiculos> buscarPorPlaca(@PathVariable String placa){
        return service.buscarPorPlaca(placa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeiculosDTO> atualizar(@PathVariable Long id, @RequestBody VeiculosDTO dto){
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public void Apagar(@PathVariable Long id){
        service.apagar(id);
    }
}
