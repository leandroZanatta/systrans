package br.com.lar.repository.projection;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DespesasFinanceirasProjection {

	private BigDecimal valorAcrescimo;
	private BigDecimal valorJuros;
	private String veiculo;

}