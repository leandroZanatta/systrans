package br.com.lar.reports.models;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FaturamentoMensalReport {

	private String descricao;
	private int agrupamento;
	private BigDecimal valorJan = BigDecimal.ZERO;
	private BigDecimal valorFev = BigDecimal.ZERO;
	private BigDecimal valorMar = BigDecimal.ZERO;
	private BigDecimal valorAbr = BigDecimal.ZERO;
	private BigDecimal valorMai = BigDecimal.ZERO;
	private BigDecimal valorJun = BigDecimal.ZERO;
	private BigDecimal valorJul = BigDecimal.ZERO;
	private BigDecimal valorAgo = BigDecimal.ZERO;
	private BigDecimal valorSet = BigDecimal.ZERO;
	private BigDecimal valorOut = BigDecimal.ZERO;
	private BigDecimal valorNov = BigDecimal.ZERO;
	private BigDecimal valorDez = BigDecimal.ZERO;
	private BigDecimal valorTotal = BigDecimal.ZERO;

}