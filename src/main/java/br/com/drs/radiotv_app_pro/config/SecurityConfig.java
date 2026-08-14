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
                // 1. ATIVA O FILTRO DE CORS CONFIGURADO ABAIXO E DETONA O ERRO DO CONSOLE
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers(HttpMethod.POST, "/api/v1/usuario/public/**").permitAll()
                        .requestMatchers("/api/v1/usuario/public/**").permitAll()
                        .requestMatchers("/api/v1/usuario/**").permitAll()
                        .requestMatchers("/api/v1/funcionario/**").permitAll()
                        .requestMatchers("/api/v1/familia/**").permitAll()
                        .requestMatchers("/api/v1/filho/**").permitAll()
                        .requestMatchers("/api/v1/vendedor/**").permitAll()
                        .requestMatchers("/api/v1/cliente/**").permitAll()
                        .requestMatchers("/api/v1/agencia/**").permitAll()
                        .requestMatchers("/api/v1/contrato/**").permitAll()
                        .requestMatchers("/api/v1/contratoMidia/**").permitAll()
                        .requestMatchers("/api/v1/contratoPagamento/**").permitAll()
                        .requestMatchers("/api/v1/programa/**").permitAll()
                        .requestMatchers("/api/v1/ramoAtividade/**").permitAll()
                        .requestMatchers("/api/v1/configEscritorio/**").permitAll()
                        .requestMatchers("/api/v1/ponto/**").permitAll()
                        .requestMatchers("/api/v1/beneficio/**").permitAll()
                        .requestMatchers("/api/v1/produto/**").permitAll()
                        .requestMatchers("/api/v1/compras/**").permitAll()
                        .requestMatchers("/api/v1/contasBancaria/**").permitAll()
                        .requestMatchers("/api/v1/feriado/**").permitAll()
                        .requestMatchers("/api/v1/ferias/**").permitAll()
                        .requestMatchers("/api/v1/folhasPagamento/**").permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // 2. CONFIGURAÇÃO EXPLÍCITA DE CORS PARA O SPRING SECURITY LIBERAR O REACT (PORTA 3000)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Origem do seu front
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica para todas as rotas da API
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