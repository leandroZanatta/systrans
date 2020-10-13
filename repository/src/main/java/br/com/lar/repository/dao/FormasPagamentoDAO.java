package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QFormasPagamento.formasPagamento;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.FormasPagamento;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.enumeradores.FormaPagamentoEnum;

public class FormasPagamentoDAO extends PesquisableDAOImpl<FormasPagamento> {

	private static final long serialVersionUID = 1L;

	public FormasPagamentoDAO() {
		super(formasPagamento, formasPagamento.idFormaPagamento);
	}

	public BooleanBuilder pesquisarApenasAPrazo() {

		return new BooleanBuilder(formasPagamento.codigoFormaPagamento.eq(FormaPagamentoEnum.BOLETO.getCodigo())
				.or(formasPagamento.flagPermitePagamentoPrazo.eq(true)));
	}

}
