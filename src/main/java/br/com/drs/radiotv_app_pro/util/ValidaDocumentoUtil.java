package br.com.drs.radiotv_app_pro.util;

public class ValidaDocumentoUtil {

    public static boolean isCPF(String cpf) {
        if (cpf == null) return false;

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.matches("^(\\d)\\1{10}$")) {
            return false;
        }

        try {
            int soma = 0;
            int peso = 10;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * peso--;
            }
            int resto = 11 - (soma % 11);
            char dv1 = (resto == 10 || resto == 11) ? '0' : (char) (resto + '0');

            soma = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * peso--;
            }
            resto = 11 - (soma % 11);
            char dv2 = (resto == 10 || resto == 11) ? '0' : (char) (resto + '0');

            return dv1 == cpf.charAt(9) && dv2 == cpf.charAt(10);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isCNPJ(String cnpj) {
        if (cnpj == null) return false;

        cnpj = cnpj.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();

        if (cnpj.length() != 14) {
            return false;
        }

        if (!Character.isDigit(cnpj.charAt(12)) || !Character.isDigit(cnpj.charAt(13))) {
            return false;
        }

        try {
            char dv1Calculado = calcularDigitoCNPJ(cnpj, 12, new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
            char dv2Calculado = calcularDigitoCNPJ(cnpj + dv1Calculado, 13, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});

            return dv1Calculado == cnpj.charAt(12) && dv2Calculado == cnpj.charAt(13);
        } catch (Exception e) {
            return false;
        }
    }

    private static char calcularDigitoCNPJ(String base, int tamanho, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            // Converte caracteres para o valor decimal correspondente (letras e números)
            int valorCaractere = base.charAt(i) - 48;
            soma += valorCaractere * pesos[i];
        }
        int resto = soma % 11;
        int resultado = 11 - resto;
        return (resultado >= 10) ? '0' : (char) (resultado + '0');
    }
}