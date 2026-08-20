package br.com.drs.radiotv_app_pro.model.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.ModalidadeCobranca;
import br.com.drs.radiotv_app_pro.model.enuns.TipoBeneficio;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "beneficios_catalogo")
public class Beneficios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeBeneficio; // ex: Unimed Participativo, Convênio Farmácia DrogaVen

    @Enumerated(EnumType.STRING)
    private TipoBeneficio tipo;

    @Enumerated(EnumType.STRING)
    private ModalidadeCobranca modalidadeCobranca;

    private BigDecimal valorFuncionario; // Valor fixo (se houver)

    private BigDecimal valorFamilia; // Valor por dependente (se houver)

    private BigDecimal percentualCoparticipacao; // Ex: 20% sobre consulta (se aplicável)

    private String observacoes;

    @Builder.Default
    private Boolean ativo = true;
}