package br.com.drs.radiotv_app_pro.model;

import br.com.drs.radiotv_app_pro.model.enuns.Formacao;
import br.com.drs.radiotv_app_pro.model.enuns.Sexo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "filho")
public class Filho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 14)
    private String cpf;

    @Column(length = 20)
    private String rg;

    @Column(length = 20)
    private String telefone;

    @Column(length = 21)
    private String celular;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Enumerated(EnumType.STRING)
    private Formacao formacao;

    @Builder.Default
    private Boolean ativo = true;
}
