package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QOperacao.operacao;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.Operacao;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.LongUtil;

public class OperacaoDAO extends PesquisableDAOImpl<Operacao> {

	private static final long serialVersionUID = 1L;

	public OperacaoDAO() {
		super(operacao, operacao.idOperacao);
	}

	public boolean validarBuscarOperacao(Operacao objetoPersistir) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		booleanBuilder.and(operacao.codigoContaCredora.eq(objetoPersistir.getContaCredora().getIdPlanoContas()));
		booleanBuilder.and(operacao.codigoContaDevedora.eq(objetoPersistir.getContaDevedora().getIdPlanoContas()));

		if (!LongUtil.isNullOrZero(objetoPersistir.getIdOperacao())) {
			booleanBuilder.and(operacao.idOperacao.ne(objetoPersistir.getIdOperacao()));
		}

		return from().where(booleanBuilder).exists();
	}

}
