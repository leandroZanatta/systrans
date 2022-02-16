package br.com.systrans.util.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class PesquisaFaturamentoVO {

	private Long codigoConta;

	private List<Long> codigoFornecedores;

	private String codigoDocumento;

	private Long codigoFormaPagamento;

	private List<Long> codigoHistoricos;

	private Long codigoVeiculo;

	private Long codigoCentroCusto;

	private Date dataMovimentoInicial;

	private Date dataMovimentoFinal;

	private BigDecimal valorInicial;

	private BigDecimal valorFinal;

}