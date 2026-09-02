package br.com.drs.radiotv_app_pro.model.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.Papel;
import br.com.drs.radiotv_app_pro.model.enuns.Setor;
import br.com.drs.radiotv_app_pro.model.enuns.Sistema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "usuario")
public class Usuario implements org.springframework.security.core.userdetails.UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuarioId;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    private String senha;

    @Column(length = 8, unique = true)
    private String chaveUsuario;

    @Builder.Default
    private Boolean acessoEscritorio = false;

    @Enumerated(EnumType.STRING)
    private Papel papeis;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_setores", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "setor")
    @Enumerated(EnumType.STRING)
    private List<Setor> setores;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_sistemas", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "sistema")
    @Enumerated(EnumType.STRING)
    private List<Sistema> sistemas;

    @Column(length = 40)
    private String chavePrimeiroAcesso;

    @Column(length = 45)
    private String chaveTrocaSenha;

    @Builder.Default
    private Boolean primeiroAcesso = true;

    private LocalDateTime acessoSistema;

    @Builder.Default
    private Boolean ativo = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @LastModifiedDate
    private LocalDateTime atualizadoEm;

    public interface UsuarioResumoProjection {
        String getNome();
        String getPapel();
    }

    // ==========================================
    // Métodos da interface UserDetails do Spring
    // ==========================================
    @Override
    public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        // Retorna o papel do usuário como autoridade do Spring Security
        if (this.papeis != null) {
            return java.util.Collections.singletonList(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + this.papeis.name())
            );
        }
        return java.util.Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email; // Ou chaveUsuario, dependendo de como seu UserDetailsService busca
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.ativo != null ? this.ativo : true;
    }
}