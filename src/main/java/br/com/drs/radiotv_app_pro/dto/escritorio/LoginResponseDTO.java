package br.com.drs.radiotv_app_pro.dto.escritorio;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;

    private UsuarioDTO usuario;
}