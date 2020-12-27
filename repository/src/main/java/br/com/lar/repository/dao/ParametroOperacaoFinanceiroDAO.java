package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QParametroOperacaoFinanceira.parametroOperacaoFinanceira;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.ParametroOperacaoFinanceira;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.LongUtil;

public class ParametroOperacaoFinanceiroDAO extends PesquisableDAOImpl<ParametroOperacaoFinanceira> {

	private static final long serialVersionUID = 1L;

	public ParametroOperacaoFinanceiroDAO() {
		super(parametroOperacaoFinanceira, parametroOperacaoFinanceira.idParametroOperacaoFinanceiro);
	}

	public ParametroOperacaoFinanceira buscarParametroOperacao(Long codigoHistorico, Long codigoTipoConta, Long codigoFormaPagamento) {

		return from().where(parametroOperacaoFinanceira.codigoTipoFinanceiro.eq(codigoHistorico)
				.and(parametroOperacaoFinanceira.codigoTipoConta.eq(codigoTipoConta)
						.and(parametroOperacaoFinanceira.codigoPagamento.eq(codigoFormaPagamento))))
				.singleResult(parametroOperacaoFinanceira);
	}

	public BooleanBuilder filtrarTipoConta(Long codigo) {

		return new BooleanBuilder(parametroOperacaoFinanceira.codigoTipoFinanceiro.eq(codigo));
	}

	public boolean existsParametroOperacao(Long idParametroOperacaoFinanceiro, Long idHistorico, Long codigoTipoConta, Long idFormaPagamento) {

		BooleanBuilder booleanBuilder = new BooleanBuilder(parametroOperacaoFinanceira.codigoTipoFinanceiro.eq(idHistorico)
				.and(parametroOperacaoFinanceira.codigoTipoConta.eq(codigoTipoConta)
						.and(parametroOperacaoFinanceira.codigoPagamento.eq(idFormaPagamento))));

		if (!LongUtil.isNullOrZero(idParametroOperacaoFinanceiro)) {

			booleanBuilder.and(parametroOperacaoFinanceira.idParametroOperacaoFinanceiro.ne(idParametroOperacaoFinanceiro));
		}

		return from().where(booleanBuilder).exists();
	}
}