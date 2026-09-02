package br.com.drs.radiotv_app_pro.controller.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ComissaoDTO;
import br.com.drs.radiotv_app_pro.dto.escritorio.ResumoComissaoVendedorDTO;
import br.com.drs.radiotv_app_pro.service.escritorio.ComissaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comissao")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComissaoController {

    private final ComissaoService service;

    @PostMapping
    public ResponseEntity<ComissaoDTO> salvar(@RequestBody ComissaoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ComissaoDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComissaoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/vendedor/{chaveUsuario}/resumo")
    public ResponseEntity<ResumoComissaoVendedorDTO> obterResumoVendedor(
            @PathVariable String chaveUsuario,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fim) {

        LocalDate dataInicio = (inicio != null) ? inicio : LocalDate.now().withDayOfMonth(1);
        LocalDate dataFim = (fim != null) ? fim : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        return ResponseEntity.ok(service.obterResumoParaFolha(chaveUsuario, dataInicio, dataFim));
    }

    @PutMapping("/{id}/pagar-vendedor")
    public ResponseEntity<ComissaoDTO> liquidarComissaoVendedor(@PathVariable Long id) {
        return ResponseEntity.ok(service.liquidarComissaoVendedor(id));
    }

    @PutMapping("/{id}/pagar-agencia")
    public ResponseEntity<ComissaoDTO> liquidarComissaoAgencia(
            @PathVariable Long id,
            @RequestParam(required = false) String numeroNota) {
        return ResponseEntity.ok(service.liquidarComissaoAgencia(id, numeroNota));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}