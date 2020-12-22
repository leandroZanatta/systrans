package br.com.lar.reports.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class FaturmaentoReport {

	private Long codigoConta;
	private String cliente;
	private Date dataMovimento;
	private BigDecimal valorTotal;
}