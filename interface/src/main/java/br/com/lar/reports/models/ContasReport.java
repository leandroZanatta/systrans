package br.com.lar.reports.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class ContasReport {

	private Long codigoConta;
	private String cliente;
	private String formaPagamento;
	private Date dataVencimento;
	private BigDecimal valorTotal;
	private BigDecimal valorDesconto;
	private BigDecimal valorAcrescimo;
	private BigDecimal valorPago;
	private BigDecimal valorLiquido;
	private String baixado;
}