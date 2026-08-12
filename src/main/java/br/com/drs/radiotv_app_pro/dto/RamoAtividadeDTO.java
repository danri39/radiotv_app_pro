package br.com.drs.radiotv_app_pro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RamoAtividadeDTO {

    private Long id;

    @NotBlank(message = "A descrição do ramo de atividade é obrigatória.")
    private String descricao; // Certifique-se de que está escrito "descricao" exatamente assim
}