package br.com.drs.radiotv_app_pro.service.radio;

import br.com.drs.radiotv_app_pro.dto.radio.ArquivoImportacaoDTO;
import br.com.drs.radiotv_app_pro.dto.radio.MusicasXmlWrapper;
import br.com.drs.radiotv_app_pro.mapper.radio.MusicasXmlMapper;
import br.com.drs.radiotv_app_pro.model.radio.Musicas;
import br.com.drs.radiotv_app_pro.repository.radio.MusicasRepository;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MusicaImportacaoService {

    private final MusicasRepository musicaRepository;
    private final MusicasXmlMapper musicasXmlMapper;
    private final XmlMapper xmlMapper;

    public ArquivoImportacaoDTO importarMusicasDoXml(String xmlContent) {
        List<String> erros = new ArrayList<>();
        int importadas = 0;

        try {
            MusicasXmlWrapper wrapper = xmlMapper.readValue(xmlContent, MusicasXmlWrapper.class);
            List<Musicas> musicas = musicasXmlMapper.toEntityList(wrapper.getMusicas());

            for (Musicas musica : musicas) {
                try {
                    Optional<Musicas> existente = Optional.empty();

                    if (musica.getId() != null) {
                        existente = musicaRepository.findById(musica.getId());
                    }

                    if (existente.isEmpty()) {
                        existente = musicaRepository.findByArtistaAndNomeMusica(
                                musica.getArtista(), musica.getNomeMusica());
                    }

                    if (existente.isPresent()) {
                        Musicas atual = existente.get();
                        musica.setId(atual.getId());
                        musica.setUltimaExecucao(atual.getUltimaExecucao());
                        musicaRepository.save(musica);
                    } else {
                        if (musica.getId() == null) {
                            musica.setId(System.currentTimeMillis());
                        }
                        musicaRepository.save(musica);
                    }

                    importadas++;

                } catch (Exception e) {
                    erros.add(STR."Erro ao importar '\{musica.getArtista()} - \{musica.getNomeMusica()}': \{e.getMessage()}");
                    log.error("Erro ao importar música", e);
                }
            }

            log.info("Importação concluída: {} importadas, {} erros", importadas, erros.size());

            return ArquivoImportacaoDTO.builder()
                    .totalMusicas(musicas.size())
                    .musicasImportadas(importadas)
                    .musicasComErro(erros.size())
                    .erros(erros)
                    .build();

        } catch (Exception e) {
            log.error("Erro geral na importação", e);
            throw new RuntimeException("Erro na importação: " + e.getMessage(), e);
        }
    }
}