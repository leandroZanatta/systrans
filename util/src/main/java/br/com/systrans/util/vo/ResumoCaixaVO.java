package br.com.systrans.util.vo;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumoCaixaVO {

	private String descricao;

	private Date dataCaixa;

	private BigDecimal valorMovimentoCredito;

	private BigDecimal valorMovimentoDebito;

	private BigDecimal valorPagamentosCredito;

	private BigDecimal valorPagamentosDebito;
}
