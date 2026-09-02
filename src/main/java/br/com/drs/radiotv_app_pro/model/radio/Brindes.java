package br.com.drs.radiotv_app_pro.model.radio;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "brindes")
public class Brindes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private String identificacao;

    private LocalDate dtSorteio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ouvintes_id", nullable = false)
    private Ouvintes ouvintes;

    private LocalDate dtBuscarBrinde;
}
