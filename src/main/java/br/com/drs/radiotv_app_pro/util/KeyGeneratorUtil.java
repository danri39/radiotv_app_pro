package br.com.drs.radiotv_app_pro.util;

import br.com.drs.radiotv_app_pro.model.enuns.Setor;

import java.security.SecureRandom;

public class KeyGeneratorUtil {

    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMEROS = "0123456789";
    private static final String CARACTERES_GERAIS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String gerarChaveUsuario(Setor setor) {
        String prefixo = (setor != null) ? setor.getSetor() : "OU";

        StringBuilder sb = new StringBuilder(prefixo.toUpperCase());

        for (int i = 0; i < 6; i++) {
            sb.append(NUMEROS.charAt(RANDOM.nextInt(NUMEROS.length())));
        }

        return sb.toString();
    }

    public static String gerarChavePrimeiroAcesso() {
        return gerar(40, CARACTERES_GERAIS).toLowerCase();
    }

    public static String gerarChaveTrocaSenha() {
        return gerar(45, CARACTERES_GERAIS).toLowerCase();
    }

    private static String gerar(int tamanho, String baseCaracteres) {
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            sb.append(baseCaracteres.charAt(RANDOM.nextInt(baseCaracteres.length())));
        }
        return sb.toString();
    }
}