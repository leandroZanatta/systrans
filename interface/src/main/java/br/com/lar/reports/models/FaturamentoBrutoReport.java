package br.com.lar.reports.models;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FaturamentoBrutoReport {

	private String descricao;
	private BigDecimal valor;
}