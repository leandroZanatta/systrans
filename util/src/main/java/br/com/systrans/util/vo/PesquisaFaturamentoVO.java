package br.com.systrans.util.vo;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class PesquisaFaturamentoVO {

	private Long codigoConta;

	private Long codigoFornecedor;

	private String codigoDocumento;

	private Long codigoFormaPagamento;

	private Long codigoHistorico;

	private Long codigoVeiculo;

	private Long codigoCentroCusto;

	private Date dataMovimentoInicial;

	private Date dataMovimentoFinal;

	private BigDecimal valorInicial;

	private BigDecimal valorFinal;

}