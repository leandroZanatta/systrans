package br.com.lar.repository.projection;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FaturamentoVeiculoProjection {

	private BigDecimal valorBruto;
	private Long codigoVeiculo;

}