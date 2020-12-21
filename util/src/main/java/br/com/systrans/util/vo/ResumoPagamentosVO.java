package br.com.systrans.util.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ResumoPagamentosVO {

	private BigDecimal valorParcelas = BigDecimal.ZERO;

	private BigDecimal valorDesconto = BigDecimal.ZERO;

	private BigDecimal valorAcrescimo = BigDecimal.ZERO;

	private BigDecimal valorJuros = BigDecimal.ZERO;

	private BigDecimal valorPagar = BigDecimal.ZERO;
}
