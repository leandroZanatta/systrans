package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QCaixaCabecalho.caixaCabecalho;
import static br.com.lar.repository.model.QCaixaSaldo.caixaSaldo;

import java.math.BigDecimal;
import java.util.Date;

import br.com.lar.repository.model.CaixaSaldo;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class CaixaSaldoDAO extends PesquisableDAOImpl<CaixaSaldo> {

	private static final long serialVersionUID = 1L;

	public CaixaSaldoDAO() {
		super(caixaSaldo, caixaSaldo.idCaixaSaldo);
	}

	public BigDecimal buscarUltimoSaldoCaixa(Long codigoCaixa) {

		return sqlFrom().innerJoin(caixaCabecalho).on(caixaSaldo.codigoCaixaCabecalho.eq(caixaCabecalho.idCaixaCabecalho))
				.where(caixaCabecalho.codigoCaixa.eq(codigoCaixa)).orderBy(caixaSaldo.dataMovimento.desc())
				.singleResult(caixaSaldo.valorSaldoAcumulado);
	}

	public BigDecimal buscarUltimoSaldoCaixa(Long codigoCaixa, Date dataAbertura) {

		return sqlFrom().innerJoin(caixaCabecalho).on(caixaSaldo.codigoCaixaCabecalho.eq(caixaCabecalho.idCaixaCabecalho))
				.where(caixaCabecalho.codigoCaixa.eq(codigoCaixa).and(caixaCabecalho.dataAbertura.lt(dataAbertura)))
				.orderBy(caixaSaldo.dataMovimento.desc()).singleResult(caixaSaldo.valorSaldoAcumulado);
	}

}
