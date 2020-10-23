package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QCaixa.caixa;
import static br.com.lar.repository.model.QCaixaCabecalho.caixaCabecalho;

import com.mysema.query.types.expr.DateExpression;

import br.com.lar.repository.model.CaixaCabecalho;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class CaixaCabecalhoDAO extends PesquisableDAOImpl<CaixaCabecalho> {

	private static final long serialVersionUID = 1L;

	public CaixaCabecalhoDAO() {
		super(caixaCabecalho, caixaCabecalho.idCaixaCabecalho);
	}

	public Boolean buscarCaixasAbertos(Long idUsuario) {

		return from().where(caixaCabecalho.caixa.codigoUsuario.eq(idUsuario).and(caixaCabecalho.dataFechamento.isNull())).exists();
	}

	public boolean isCaixaAberto(Long idCaixaCabecalho) {

		return from().where(caixaCabecalho.idCaixaCabecalho.eq(idCaixaCabecalho).and(caixaCabecalho.dataFechamento.isNull())).exists();
	}

	public CaixaCabecalho obterCaixa(Long codigoUsuario) {

		return from().innerJoin(caixa).on(caixaCabecalho.codigoCaixa.eq(caixa.idCaixa))
				.where(caixa.codigoUsuario.eq(codigoUsuario).and(caixaCabecalho.dataMovimento.eq(DateExpression.currentDate()))
						.and(caixaCabecalho.dataFechamento.isNull()))
				.singleResult(caixaCabecalho);
	}

}
