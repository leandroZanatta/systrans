package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QOperacaoFinanceiro.operacaoFinanceiro;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.OperacaoFinanceiro;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.LongUtil;

public class OperacaoFinanceiroDAO extends PesquisableDAOImpl<OperacaoFinanceiro> {

	private static final long serialVersionUID = 1L;

	public OperacaoFinanceiroDAO() {
		super(operacaoFinanceiro, operacaoFinanceiro.idOperacaoFinanceiro);
	}

	public boolean validarBuscarOperacao(OperacaoFinanceiro objetoPersistir) {
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		booleanBuilder.and(operacaoFinanceiro.codigoHistorico.eq(objetoPersistir.getHistorico().getIdHistorico()));
		booleanBuilder.and(operacaoFinanceiro.codigoContaCredora.eq(objetoPersistir.getContaCredora().getIdPlanoContas()));
		booleanBuilder.and(operacaoFinanceiro.codigoContaDevedora.eq(objetoPersistir.getContaDevedora().getIdPlanoContas()));

		if (!LongUtil.isNullOrZero(objetoPersistir.getIdOperacaoFinanceiro())) {
			booleanBuilder.and(operacaoFinanceiro.idOperacaoFinanceiro.ne(objetoPersistir.getIdOperacaoFinanceiro()));
		}

		return from().where(booleanBuilder).exists();
	}

	public OperacaoFinanceiro buscarPorPlanoCredorEFormaPagamento(Long codigoPlanoContas, Long idFormaPagamento) {

		return from().where(operacaoFinanceiro.codigoPagamento.eq(idFormaPagamento).and(operacaoFinanceiro.codigoContaCredora.eq(codigoPlanoContas)))
				.singleResult(operacaoFinanceiro);
	}

}
