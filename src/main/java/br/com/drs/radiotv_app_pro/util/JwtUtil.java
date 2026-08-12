package br.com.drs.radiotv_app_pro.util;

import br.com.drs.radiotv_app_pro.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    // Geramos uma chave segura com tamanho suficiente para o algoritmo HS256
    private static final Key CHAVE_SECRETA = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Tempo de expiração: 1 dia (em milissegundos)
    private static final long EXPIRATION_TIME = 86400000;

    public String gerarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();

        // Guardando o nome puro do Enum Papel (Ex: "ADMINISTRADOR", "GERENTE")
        claims.put("papel", usuario.getPapeis().name());

        // Convertendo a lista de Enums Setor para uma lista de Strings (Ex: ["ESCRITORIO", "FINANCEIRO"])
        claims.put("setores", usuario.getSetores().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));

        // Guardando o booleano de acesso ao escritório
        claims.put("acessoEscritorio", usuario.getAcessoEscritorio());
        claims.put("nomeUsuario", usuario.getNome());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(CHAVE_SECRETA)
                .compact();
    }

    public String extrairEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(CHAVE_SECRETA)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject(); // Recupera o e-mail que salvamos no setSubject
        } catch (Exception e) {
            return null; // Token inválido, expirado ou violado
        }
    }
}