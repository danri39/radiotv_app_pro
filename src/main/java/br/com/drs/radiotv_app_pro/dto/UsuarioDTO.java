package br.com.drs.radiotv_app_pro.dto;

import br.com.drs.radiotv_app_pro.model.enuns.Papel;
import br.com.drs.radiotv_app_pro.model.enuns.Setor;
import br.com.drs.radiotv_app_pro.model.enuns.Sistema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

    private Long usuarioId;

    private String nome;

    private String email;

    private String senha;

    private String chaveUsuario;

    @Builder.Default
    private Boolean acessoEscritorio = false;

    private Papel papeis;

    private List<Setor> setores;

    private List<Sistema> sistemas;

    private String chavePrimeiroAcesso;

    private String chaveTrocaSenha;

    @Builder.Default
    private Boolean primeiroAcesso = true;

    private LocalDateTime acessoSistema;

    private Long funcionarioId;

    @Builder.Default
    private Boolean ativo = true;
}
