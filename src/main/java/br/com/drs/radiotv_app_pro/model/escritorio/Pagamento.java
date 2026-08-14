package br.com.drs.radiotv_app_pro.model.escritorio;

import br.com.drs.radiotv_app_pro.model.enuns.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataVencimento;

    private BigDecimal valor;

    private String numeroDocumento;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    private BigDecimal valorPagamentoEfetivo;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder.Default
    private Boolean ativo = true;
}
