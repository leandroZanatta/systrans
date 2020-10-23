package br.com.lar.repository.projection;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ResumoCaixaDetalheProjection {

	private Long tipoSaldo;

	private BigDecimal valorSaldo;
}
