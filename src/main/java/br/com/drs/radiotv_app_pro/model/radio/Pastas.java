package br.com.drs.radiotv_app_pro.model.radio;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "pastas")
public class Pastas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String arquivos;

    private String chamadas;

    private String comerciais;

    private String componentes;

    private String ferramentas;

    private String horaCerta;

    private String locucao;

    private String mapasIrradiacao;

    private String musicas;

    private String retornoBanco;

    private String roteiroComercial;

    private String roteiroMusical;

    private String trilhas;

    private String vinhetas;
}
