package br.com.drs.radiotv_app_pro.service.radio;

import br.com.drs.radiotv_app_pro.dto.radio.RegistroComercialDTO;
import br.com.drs.radiotv_app_pro.mapper.radio.RegistroComercialMapper;
import br.com.drs.radiotv_app_pro.model.radio.RegistroComercial;
import br.com.drs.radiotv_app_pro.repository.radio.RegistroComercialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistroComercialService {

    private final RegistroComercialRepository repository;
    private final RegistroComercialMapper mapper;

    public RegistroComercialDTO salvar(RegistroComercialDTO dto) {
        RegistroComercial comercialNovo = mapper.toEntity(dto);
        repository.save(comercialNovo);
        return mapper.toDto(comercialNovo);
    }

    public RegistroComercialDTO buscarPorId(String id) {
        RegistroComercial comercial = repository.findByIdAndAtivoTrue(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException(STR."Registro Comercial não encontrado ou inativo para o ID: \{id}"));
        return mapper.toDto(comercial);
    }

    public RegistroComercialDTO atualizar(String id, RegistroComercialDTO dto) {
        dto.setId(String.valueOf(id));
        RegistroComercial existentes = mapper.toEntity(dto);
        mapper.updateEntityFromDto(dto, existentes);
        return mapper.toDto(existentes);
    }

    public RegistroComercialDTO inativar(String id, RegistroComercialDTO dto) {
        dto.setAtivo(false);

        RegistroComercial entity = mapper.toEntity(dto);
        entity.setId(String.valueOf(id));

        RegistroComercial salvo = repository.save(entity);

        return mapper.toDto(salvo);
    }
}
