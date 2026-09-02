package br.com.drs.radiotv_app_pro.dto.radio;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicasXmlDTO {

    private Long id;

    @Builder.Default
    private String artista = "";

    @Builder.Default
    private String nomeMusica = "";

    @Builder.Default
    private String compositor = "";

    @Builder.Default
    private String tempoMusica = "";

    @Builder.Default
    private String introducao = "";

    @Builder.Default
    private String anoLancamento = "";

    @Builder.Default
    private String genero = "";

    @Builder.Default
    private String lancamento = "";

    @Builder.Default
    private String periodos = "";

    @Builder.Default
    private String diasSemana = "";

    @Builder.Default
    private String repetir = "";

    @Builder.Default
    private Integer quantidade = 0;

    @Builder.Default
    private Boolean nacional = false;

    @Builder.Default
    private String observacao = "";

    @Builder.Default
    private Boolean ativa = true;

    /**
     * Limpa caracteres inválidos de XML (\u0000, etc.) e aplica Title Case.
     * Ex: "BARÃO VERMELHO" -> "Barão Vermelho"
     * Ex: "hey jude" -> "Hey Jude"
     */
    public static String formatarESanear(String texto) {
        if (texto == null) {
            return "";
        }

        // 1. Remove caracteres de controle inválidos para XML (incluindo null char \u0000)
        // Mantém apenas caracteres imprimíveis, espaços, tabulações e quebras de linha
        String limpo = texto.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "").trim();

        if (limpo.isEmpty()) {
            return "";
        }

        // 2. Aplica Title Case (Primeira letra maiúscula, resto minúsculo)
        StringBuilder sb = new StringBuilder();
        boolean proximaMaiuscula = true;

        for (char c : limpo.toCharArray()) {
            if (Character.isWhitespace(c)) {
                proximaMaiuscula = true;
                sb.append(c);
            } else if (proximaMaiuscula) {
                sb.append(Character.toUpperCase(c));
                proximaMaiuscula = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }

        return sb.toString();
    }
}