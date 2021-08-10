package br.com.systrans.util.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class PesquisaCentroCustoVO {

	private List<Long> codigoHistoricos;

	private List<Long> codigoVeiculos;

	private List<Long> codigoCentroCustos;

	private List<Long> despesas;

	private Date dataMovimentoInicial;

	private Date dataMovimentoFinal;

}