package br.com.drs.radiotv_app_pro.controller.radio;

import br.com.drs.radiotv_app_pro.dto.radio.OuvintesDTO;
import br.com.drs.radiotv_app_pro.service.radio.OuvintesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ouvintes")
@RequiredArgsConstructor
public class OuvintesController {

    private final OuvintesService service;

    @PostMapping
    public ResponseEntity<OuvintesDTO> create(OuvintesDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

}
