package br.com.systrans.util.vo;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FaturamentoBrutoVO {

	private String descricao;
	private BigDecimal valorContabil;
	private BigDecimal valorSocial;
	private Integer agrupamento;
	private FaturamentoBrutoVO parent;
	private BigDecimal percentual = BigDecimal.ZERO;

	public FaturamentoBrutoVO(String descricao, BigDecimal valorContabil, BigDecimal valorSocial, Integer agrupamento, FaturamentoBrutoVO parent) {
		this.descricao = descricao;
		this.valorContabil = valorContabil;
		this.valorSocial = valorSocial;
		this.agrupamento = agrupamento;
		this.parent = parent;
	}
}