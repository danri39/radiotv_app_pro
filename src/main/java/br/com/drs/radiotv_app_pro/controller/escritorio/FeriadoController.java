package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.model.escritorio.Feriado;
import br.com.drs.radiotv_app_pro.repository.escritorio.FeriadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feriado")
@CrossOrigin(origins = "*")
public class FeriadoController {

    @Autowired
    private FeriadoRepository repository;

    @GetMapping
    public List<Feriado> listarTodos() {
        return repository.findAll();
    }

    @PostMapping
    public Feriado cadastrar(@RequestBody Feriado feriado) {
        return repository.save(feriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feriado> atualizar(@PathVariable Long id, @RequestBody Feriado dadosAtualizados) {
        return repository.findById(id)
                .map(feriado -> {
                    feriado.setDescricao(dadosAtualizados.getDescricao());
                    feriado.setDataFeriado(dadosAtualizados.getDataFeriado()); // Atualiza a data explicitamente
                    Feriado atualizado = repository.save(feriado);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}