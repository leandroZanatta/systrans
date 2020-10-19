package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QCaixa.caixa;
import static br.com.lar.repository.model.QCaixaCabecalho.caixaCabecalho;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Projections;
import com.mysema.query.types.expr.DateExpression;

import br.com.lar.repository.model.Caixa;
import br.com.lar.repository.projection.CaixaAbertoProjection;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.LongUtil;

public class CaixaDAO extends PesquisableDAOImpl<Caixa> {

	private static final long serialVersionUID = 1L;

	public CaixaDAO() {
		super(caixa, caixa.idCaixa);
	}

	public boolean existeCaixaUsuario(Caixa objetoPersistir) {

		BooleanBuilder booleanBuilder = new BooleanBuilder(caixa.usuario.eq(objetoPersistir.getUsuario()));

		if (!LongUtil.isNullOrZero(objetoPersistir.getIdCaixa())) {

			booleanBuilder.and(caixa.idCaixa.ne(objetoPersistir.getIdCaixa()));
		}

		return from().where(booleanBuilder).exists();
	}

	public CaixaAbertoProjection obterCaixaAberto(Long idUsuario) {

		return sqlFrom().leftJoin(caixaCabecalho)
				.on(caixaCabecalho.codigoCaixa.eq(caixa.idCaixa).and(caixaCabecalho.dataMovimento.eq(DateExpression.currentDate())))
				.where(caixa.codigoUsuario.eq(idUsuario))
				.singleResult(Projections.fields(CaixaAbertoProjection.class, caixa.descricao,
						caixaCabecalho.idCaixaCabecalho.isNotNull().as("caixaAberto")));
	}

	public Caixa buscarCaixaPorUsuario(Long idUsuario) {

		return from().where(caixa.codigoUsuario.eq(idUsuario)).singleResult(caixa);
	}

}
