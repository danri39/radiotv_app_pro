package br.com.drs.radiotv_app_pro.model.escritorio;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ContratoMidiaAudioPool {
    private String arquivo;
    private Integer insercoes;
}