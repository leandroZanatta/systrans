package br.com.systrans.util.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FaturamentoBrutoVO {

	private BigDecimal totalReceitas;

	private BigDecimal totalDespesas;

	private BigDecimal valorFaturamento;

}