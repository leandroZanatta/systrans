package br.com.lar.repository.projection;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class PagamentoContasProjection<T> {

	private Long idConta;

	private String cliente;

	private Date vencimento;

	private BigDecimal valorParcela;

	private BigDecimal decontos = BigDecimal.ZERO;

	private BigDecimal acrescimos = BigDecimal.ZERO;

	private BigDecimal juros = BigDecimal.ZERO;

	private BigDecimal valorPagar;

	private T conta;
}
