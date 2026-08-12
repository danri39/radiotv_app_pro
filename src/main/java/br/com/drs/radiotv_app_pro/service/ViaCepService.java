package br.com.drs.radiotv_app_pro.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    // Classe interna simples apenas para mapear o retorno da API do ViaCEP
    @Getter
    @Setter
    public static class ViaCepDTO {
        private String logradouro;
        private String bairro;
        private String localidade; // Cidade
        private String uf;         // Estado
        private Boolean erro;
    }

    public ViaCepDTO buscarEnderecoPorCep(String cep) {
        if (cep == null) return null;

        // Remove traços ou espaços que o front possa ter enviado
        String cepLimpo = cep.replaceAll("\\D", "");

        if (cepLimpo.length() != 8) {
            return null; // CEP inválido, ignora a busca automática
        }

        try {
            String url = "https://viacep.com.br/ws/" + cepLimpo + "/json/";
            RestTemplate restTemplate = new RestTemplate();
            ViaCepDTO resultado = restTemplate.getForObject(url, ViaCepDTO.class);

            if (resultado != null && resultado.getErro() != null && resultado.getErro()) {
                return null; // CEP não encontrado na base dos Correios
            }

            return resultado;
        } catch (Exception e) {
            // Se a API do ViaCEP estiver fora do ar, o sistema não trava o cadastro
            return null;
        }
    }
}