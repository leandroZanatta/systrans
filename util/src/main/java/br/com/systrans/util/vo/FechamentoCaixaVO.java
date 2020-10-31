package br.com.systrans.util.vo;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class FechamentoCaixaVO {

	private BigDecimal saldoAtual;

	private BigDecimal valorMovimentado;

	private BigDecimal valorPagamentos;

	private BigDecimal valorDinheiro;

	private List<DetalheFechamentoVO> detalheFechamentoVOs;
}
