package br.com.drs.radiotv_app_pro.dto.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.Formacao;
import br.com.drs.radiotv_app_pro.model.enuns.Sexo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilhoDTO {

    private Long id;

    private Long funcionarioId;

    private String nome;

    private String cpf;

    private String rg;

    private String telefone;

    private String celular;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    private Sexo sexo;

    private Formacao formacao;

    private Boolean ativo;
}
