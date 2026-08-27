package br.com.drs.radiotv_app_pro.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 1. Liberação global de requisições de preflight do CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Rotas públicas e de autenticação
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuario/public/**").permitAll()
                        .requestMatchers("/api/v1/usuario/public/**").permitAll()

                        // 3. Rotas dos Módulos do Sistema
                        .requestMatchers("/api/v1/usuario/**").permitAll()
                        .requestMatchers("/api/v1/funcionario/**").permitAll()
                        .requestMatchers("/api/v1/familia/**").permitAll()
                        .requestMatchers("/api/v1/filho/**").permitAll()
                        .requestMatchers("/api/v1/vendedor/**").permitAll()
                        .requestMatchers("/api/v1/cliente/**").permitAll()
                        .requestMatchers("/api/v1/agencia/**").permitAll()
                        .requestMatchers("/api/v1/contrato/**").permitAll()
                        .requestMatchers("/api/v1/contratoMidia/**").permitAll()
                        .requestMatchers("/api/v1/faturamento/**").permitAll()
                        .requestMatchers("/api/v1/programa/**").permitAll()
                        .requestMatchers("/api/v1/ramoAtividade/**").permitAll()
                        .requestMatchers("/api/v1/configEscritorio/**").permitAll()
                        .requestMatchers("/api/v1/pontos/**").permitAll()
                        .requestMatchers("/api/v1/beneficio/**").permitAll()
                        .requestMatchers("/api/v1/produto/**").permitAll()
                        .requestMatchers("/api/v1/compras/**").permitAll()
                        .requestMatchers("/api/v1/contasBancaria/**").permitAll()
                        .requestMatchers("/api/v1/feriado/**").permitAll()
                        .requestMatchers("/api/v1/ferias/**").permitAll()
                        .requestMatchers("/api/v1/folhasPagamento/**").permitAll()
                        .requestMatchers("/api/v1/horariosBreaks/**").permitAll()
                        .requestMatchers("/api/v1/pagamento/**").permitAll()
                        .requestMatchers("/api/v1/administracao/**").permitAll()
                        .requestMatchers("/api/v1/comissao/**").permitAll()
                        .requestMatchers("/api/v1/veiculos/**").permitAll()
                        .requestMatchers("/api/v1/frota/**").permitAll()
                        .requestMatchers("/api/v1/funcionarioBeneficio/**").permitAll()
                        .requestMatchers("/api/v1/gerencial/**").permitAll()
                        .requestMatchers("/api/v1/roteiro/**").permitAll()
                        .requestMatchers("/api/v1/music/**").permitAll()
                        .requestMatchers("/api/v1/musicas/**").permitAll()
                        .requestMatchers("/api/v1/pastas/**").permitAll()
                        .requestMatchers("/api/v1/registroComercial/**").permitAll()
                        .requestMatchers("/api/v1/registroMusical/**").permitAll()
                        .requestMatchers("/api/v1/roteiroMusical/**").permitAll()

                        // 4. Regras administrativas / restritas
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}