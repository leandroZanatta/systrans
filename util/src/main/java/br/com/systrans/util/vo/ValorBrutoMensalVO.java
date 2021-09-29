package br.com.systrans.util.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValorBrutoMensalVO {

	private int mesReferencia;
	private BigDecimal valor;
	private Long codigoVeiculo;
	private String historico;

}