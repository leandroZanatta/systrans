package br.com.systrans.util.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class PesquisaFaturamentoBrutoVO {

	private List<Long> codigoHistoricos;

	private List<Long> codigoVeiculos;

	private List<Long> codigoCentroCustos;

	private Date dataMovimentoInicial;

	private Date dataMovimentoFinal;

	private boolean usaAlocacaoCusto;

	private int codigoRelatorio;

}