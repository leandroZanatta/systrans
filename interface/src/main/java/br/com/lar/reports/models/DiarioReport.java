package br.com.lar.reports.models;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DiarioReport {

	private String dataMovimento;
	private String descricaoHistorico;
	private String identificador;
	private String descricaoConta;
	private String tipoSaldo;
	private BigDecimal valorSaldo;

}