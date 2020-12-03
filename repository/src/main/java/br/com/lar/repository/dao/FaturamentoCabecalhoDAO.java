package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QFaturamentoCabecalho.faturamentoCabecalho;

import java.math.BigDecimal;

import br.com.lar.repository.model.FaturamentoCabecalho;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class FaturamentoCabecalhoDAO extends PesquisableDAOImpl<FaturamentoCabecalho> {

	private static final long serialVersionUID = 1L;

	public FaturamentoCabecalhoDAO() {
		super(faturamentoCabecalho, faturamentoCabecalho.idFaturamentoCabecalho);
	}

	public BigDecimal buscarValorFaturamentoCaixa(Long idCaixaCabecalho) {

		return from().where(faturamentoCabecalho.caixaCabecalho.idCaixaCabecalho.eq(idCaixaCabecalho))
				.singleResult(faturamentoCabecalho.valorBruto.sum());
	}

}
