package br.com.drs.radiotv_app_pro.model.radio;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    private String mapaIrradiacao;

    private String musicas;

    private String retornoBanco;

    private String roteiroComercial;

    private String roteiroMusical;

    private String trilhas;

    private String vinhetas;
}
