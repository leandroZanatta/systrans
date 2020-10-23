package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QFormasPagamento.formasPagamento;
import static br.com.lar.repository.model.QOperacao.operacao;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.FormasPagamento;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.enumeradores.FormaPagamentoEnum;

public class FormasPagamentoDAO extends PesquisableDAOImpl<FormasPagamento> {

	private static final long serialVersionUID = 1L;

	public FormasPagamentoDAO() {
		super(formasPagamento, formasPagamento.idFormaPagamento);
	}

	public BooleanBuilder buscarPagamentosComHistorico(Long codigoHistorico) {

		return new BooleanBuilder(subQuery().from(operacao)
				.where(formasPagamento.idFormaPagamento.eq(operacao.codigoFormaPagamento).and(operacao.codigoHistorico.eq(codigoHistorico)))
				.exists());
	}

	public BooleanBuilder buscarPagamentosComHistoricoAPrazo(Long codigoHistorico) {

		return new BooleanBuilder(subQuery().from(operacao)
				.where(formasPagamento.idFormaPagamento.eq(operacao.codigoFormaPagamento).and(operacao.codigoHistorico.eq(codigoHistorico)))
				.exists()).and(formasPagamento.codigoFormaPagamento.eq(FormaPagamentoEnum.BOLETO.getCodigo())
						.or(formasPagamento.flagPermitePagamentoPrazo.eq(true)));
	}

	public BooleanBuilder pesquisarApenasAPrazo() {

		return new BooleanBuilder(formasPagamento.codigoFormaPagamento.eq(FormaPagamentoEnum.BOLETO.getCodigo())
				.or(formasPagamento.flagPermitePagamentoPrazo.eq(true)));
	}

}
