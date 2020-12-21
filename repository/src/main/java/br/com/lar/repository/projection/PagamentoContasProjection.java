package br.com.lar.repository.projection;

import java.math.BigDecimal;
import java.util.Date;

import br.com.lar.repository.model.ContasPagar;
import lombok.Data;

@Data
public class PagamentoContasProjection {

	private Long idContasPagar;

	private String cliente;

	private Date vencimento;

	private BigDecimal valorParcela;

	private BigDecimal decontos = BigDecimal.ZERO;

	private BigDecimal acrescimos = BigDecimal.ZERO;

	private BigDecimal juros = BigDecimal.ZERO;

	private BigDecimal valorPagar;

	private ContasPagar contasPagar;
}
