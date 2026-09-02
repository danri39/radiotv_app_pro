package br.com.drs.radiotv_app_pro.controller.radio;

import br.com.drs.radiotv_app_pro.dto.radio.ArquivoImportacaoDTO;
import br.com.drs.radiotv_app_pro.service.radio.MusicaArquivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/musicas/importacao")
@RequiredArgsConstructor
public class MusicaImportacaoController {

    private final MusicaArquivoService musicaArquivoService;

    @PostMapping("/gerar-xml")
    public ResponseEntity<ArquivoImportacaoDTO> gerarXmlDasMusicas() {
        ArquivoImportacaoDTO resultado = musicaArquivoService.lerPastaMusicas();
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/importar-xml")
    public ResponseEntity<ArquivoImportacaoDTO> importarXmlDaPasta() {
        ArquivoImportacaoDTO resultado = musicaArquivoService.importarXmlDaPasta();
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/upload")
    public ResponseEntity<ArquivoImportacaoDTO> uploadXml(@RequestParam("arquivo") MultipartFile arquivo) {
        try {
            String xmlContent = new String(arquivo.getBytes(), StandardCharsets.UTF_8);
            ArquivoImportacaoDTO resultado = musicaArquivoService.importarMusicasDoXml(xmlContent);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar arquivo: " + e.getMessage(), e);
        }
    }
}