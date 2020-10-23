package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QFaturamento.faturamento;

import java.math.BigDecimal;

import br.com.lar.repository.model.Faturamento;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class FaturamentoDAO extends PesquisableDAOImpl<Faturamento> {

	private static final long serialVersionUID = 1L;

	public FaturamentoDAO() {
		super(faturamento, faturamento.idFaturamento);
	}

	public BigDecimal buscarValorFaturamentoCaixa(Long idCaixaCabecalho) {

		return from().where(faturamento.caixaCabecalho.idCaixaCabecalho.eq(idCaixaCabecalho))
				.singleResult(faturamento.valorBruto.add(faturamento.valorAcrescimo).subtract(faturamento.valorDesconto).sum());
	}

}
