package br.com.lar.reports.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class FaturamentoReport {

	private Long codigoConta;
	private String veiculo;
	private String cliente;
	private Date dataMovimento;
	private BigDecimal valorTotal;
}