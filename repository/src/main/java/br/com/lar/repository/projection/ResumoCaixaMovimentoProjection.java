package br.com.lar.repository.projection;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ResumoCaixaMovimentoProjection {

	private String tipoSaldo;

	private BigDecimal valorSaldo;
}
