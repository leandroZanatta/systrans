package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QDiarioCabecalho.diarioCabecalho;
import static br.com.lar.repository.model.QDiarioDetalhe.diarioDetalhe;
import static br.com.lar.repository.model.QHistorico.historico;
import static br.com.lar.repository.model.QPlanoContas.planoContas;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.types.Projections;
import com.mysema.query.types.expr.CaseBuilder;
import com.mysema.query.types.expr.DateExpression;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.StringExpression;

import br.com.lar.repository.model.DiarioCabecalho;
import br.com.lar.repository.projection.DiarioReportProjection;
import br.com.lar.repository.projection.ResumoCaixaMovimentoProjection;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.enumeradores.TipoSaldoEnum;

public class DiarioCabecalhoDAO extends PesquisableDAOImpl<DiarioCabecalho> {

	private static final long serialVersionUID = 1L;

	public DiarioCabecalhoDAO() {
		super(diarioCabecalho, diarioCabecalho.idDiarioCabecalho);
	}

	public List<ResumoCaixaMovimentoProjection> buscarResumoCaixa(Long codigoCaixaCabecalho) {

		return sqlFrom().innerJoin(diarioDetalhe).on(diarioCabecalho.idDiarioCabecalho.eq(diarioDetalhe.codigoDiarioCabecalho)).innerJoin(planoContas)
				.on(diarioDetalhe.codigoPlanoContas.eq(planoContas.idPlanoContas))
				.where(diarioCabecalho.codigoCaixaCabecalho.eq(codigoCaixaCabecalho)
						.and(planoContas.saldo.in(TipoSaldoEnum.CREDOR.getCodigo(), TipoSaldoEnum.DEVEDOR.getCodigo())))
				.groupBy(planoContas.saldo).list(Projections.fields(ResumoCaixaMovimentoProjection.class, planoContas.saldo.as("tipoSaldo"),
						diarioDetalhe.valorDetalhe.sum().as("valorSaldo")));
	}

	public List<DiarioReportProjection> buscarDiarioPeriodo(Date dataInicial, Date dataFinal) {

		StringExpression tipoSaldo = new CaseBuilder().when(diarioDetalhe.tipoSaldo.eq(1L)).then("C").otherwise("D");

		NumberExpression<BigDecimal> valorSaldo = new CaseBuilder().when(diarioDetalhe.tipoSaldo.eq(1L)).then(diarioDetalhe.valorDetalhe.sum())
				.otherwise(diarioDetalhe.valorDetalhe.sum().negate());
		DateExpression<Date> dataMovimento = SQLExpressions.date(diarioCabecalho.dataMovimento);

		return sqlFrom().innerJoin(diarioDetalhe).on(diarioCabecalho.idDiarioCabecalho.eq(diarioDetalhe.codigoDiarioCabecalho)).innerJoin(historico)
				.on(diarioCabecalho.codigoHistorico.eq(historico.idHistorico)).innerJoin(planoContas)
				.on(diarioDetalhe.codigoPlanoContas.eq(planoContas.idPlanoContas))
				.where(dataMovimento.between(dataInicial, dataFinal))
				.groupBy(dataMovimento, diarioDetalhe.tipoSaldo, historico.descricao, planoContas.identificador,
						planoContas.descricao)
				.orderBy(dataMovimento.asc(), historico.descricao.asc(), diarioDetalhe.tipoSaldo.asc())
				.list(Projections.fields(DiarioReportProjection.class, dataMovimento.as("dataMovimento"),
						historico.descricao.as("descricaoHistorico"),
						planoContas.identificador.as("identificador"), planoContas.descricao.as("descricaoConta"),
						tipoSaldo.as("tipoSaldo"), valorSaldo.as("valorSaldo")));
	}

}
