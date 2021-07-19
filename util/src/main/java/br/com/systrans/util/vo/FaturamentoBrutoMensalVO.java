package br.com.systrans.util.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FaturamentoBrutoMensalVO {

	private int mesReferencia;
	private int ordem;
	private String descricao;
	private BigDecimal valor;
	private Integer agrupamento;
}