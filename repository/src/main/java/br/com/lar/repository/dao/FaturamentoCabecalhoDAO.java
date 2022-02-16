package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QCliente.cliente;
import static br.com.lar.repository.model.QFaturamentoCabecalho.faturamentoCabecalho;
import static br.com.lar.repository.model.QFaturamentoDetalhe.faturamentoDetalhe;
import static br.com.lar.repository.model.QHistorico.historico;
import static br.com.lar.repository.model.QVeiculo.veiculo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.sql.JPASQLQuery;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.Projections;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.TemporalExpression;

import br.com.lar.repository.model.FaturamentoCabecalho;
import br.com.lar.repository.projection.FaturamentoBrutoReportProjection;
import br.com.lar.repository.projection.FaturamentoProjection;
import br.com.lar.repository.projection.FaturamentoVeiculoProjection;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoVO;
import br.com.systrans.util.vo.ValorBrutoMensalVO;

public class FaturamentoCabecalhoDAO extends PesquisableDAOImpl<FaturamentoCabecalho> {

	private static final long serialVersionUID = 1L;

	public FaturamentoCabecalhoDAO() {
		super(faturamentoCabecalho, faturamentoCabecalho.idFaturamentoCabecalho);
	}

	public BigDecimal buscarValorFaturamentoCaixa(Long idCaixaCabecalho) {

		return from().where(faturamentoCabecalho.caixaCabecalho.idCaixaCabecalho.eq(idCaixaCabecalho))
				.singleResult(faturamentoCabecalho.valorBruto.sum());
	}

	public List<ValorBrutoMensalVO> filtrarFaturamentoBrutoBasicoMensal(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		BooleanBuilder booleanBuilder = executarPreFilter(pesquisaFaturamentoBrutoVO);

		JPASQLQuery query = sqlFrom().leftJoin(faturamentoDetalhe)
				.on(faturamentoCabecalho.idFaturamentoCabecalho.eq(faturamentoDetalhe.codigoFaturamentoCabecalho));

		if (booleanBuilder.hasValue()) {

			query.where(booleanBuilder);
		}

		NumberExpression<Integer> extractMonth = faturamentoCabecalho.dataMovimento.month();

		query.groupBy(extractMonth);

		return query.list(Projections.fields(ValorBrutoMensalVO.class,
				faturamentoDetalhe.valorBruto.add(faturamentoDetalhe.valorAcrescimo).subtract(faturamentoDetalhe.valorDesconto).sum().as("valor"),
				extractMonth.as("mesReferencia")));
	}

	public List<ValorBrutoMensalVO> filtrarFaturamentoBrutoHistoricoMensal(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {
		BooleanBuilder booleanBuilder = executarPreFilter(pesquisaFaturamentoBrutoVO);

		JPASQLQuery query = sqlFrom().leftJoin(faturamentoDetalhe)
				.on(faturamentoCabecalho.idFaturamentoCabecalho.eq(faturamentoDetalhe.codigoFaturamentoCabecalho)).innerJoin(historico)
				.on(faturamentoCabecalho.codigoHistorico.eq(historico.idHistorico));

		if (booleanBuilder.hasValue()) {

			query.where(booleanBuilder);
		}

		NumberExpression<Integer> extractMonth = faturamentoCabecalho.dataMovimento.month();

		query.groupBy(extractMonth, historico.descricao);

		return query.list(Projections.fields(ValorBrutoMensalVO.class, historico.descricao.as("historico"),
				faturamentoDetalhe.valorBruto.add(faturamentoDetalhe.valorAcrescimo).subtract(faturamentoDetalhe.valorDesconto).sum().as("valor"),
				extractMonth.as("mesReferencia")));
	}

	public BigDecimal filtrarFaturamentoBrutoBasico(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		BooleanBuilder booleanBuilder = executarPreFilter(pesquisaFaturamentoBrutoVO);

		JPASQLQuery query = sqlFrom().leftJoin(faturamentoDetalhe)
				.on(faturamentoCabecalho.idFaturamentoCabecalho.eq(faturamentoDetalhe.codigoFaturamentoCabecalho));

		if (booleanBuilder.hasValue()) {

			query.where(booleanBuilder);
		}

		return query
				.singleResult(faturamentoDetalhe.valorBruto.add(faturamentoDetalhe.valorAcrescimo).subtract(faturamentoDetalhe.valorDesconto).sum());
	}

	public List<FaturamentoBrutoReportProjection> filtrarFaturamentoBrutoDetalhado(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		BooleanBuilder booleanBuilder = executarPreFilter(pesquisaFaturamentoBrutoVO);

		JPASQLQuery query = sqlFrom().leftJoin(faturamentoDetalhe)
				.on(faturamentoCabecalho.idFaturamentoCabecalho.eq(faturamentoDetalhe.codigoFaturamentoCabecalho)).leftJoin(veiculo)
				.on(faturamentoDetalhe.codigoVeiculo.eq(veiculo.idVeiculo)).leftJoin(historico)
				.on(faturamentoCabecalho.codigoHistorico.eq(historico.idHistorico));

		if (booleanBuilder.hasValue()) {

			query.where(booleanBuilder);
		}

		query.groupBy(historico.descricao, veiculo.placa.coalesce("TODOS"));

		return query.list(Projections.fields(FaturamentoBrutoReportProjection.class, historico.descricao.as("historico"),
				veiculo.placa.coalesce("TODOS").as("veiculo"), faturamentoDetalhe.valorBruto.add(faturamentoDetalhe.valorAcrescimo)
						.subtract(faturamentoDetalhe.valorDesconto).sum().as("valorBruto")));
	}

	public List<FaturamentoBrutoReportProjection> filtrarFaturamentoBrutoHistorico(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		BooleanBuilder booleanBuilder = executarPreFilter(pesquisaFaturamentoBrutoVO);

		JPASQLQuery query = sqlFrom().leftJoin(faturamentoDetalhe)
				.on(faturamentoCabecalho.idFaturamentoCabecalho.eq(faturamentoDetalhe.codigoFaturamentoCabecalho)).leftJoin(veiculo)
				.on(faturamentoDetalhe.codigoVeiculo.eq(veiculo.idVeiculo)).leftJoin(historico)
				.on(faturamentoCabecalho.codigoHistorico.eq(historico.idHistorico));

		if (booleanBuilder.hasValue()) {

			query.where(booleanBuilder);
		}

		query.groupBy(historico.descricao);

		return query
				.list(Projections.fields(FaturamentoBrutoReportProjection.class, historico.descricao.as("historico"), faturamentoDetalhe.valorBruto
						.add(faturamentoDetalhe.valorAcrescimo).subtract(faturamentoDetalhe.valorDesconto).sum().as("valorBruto")));

	}

	private BooleanBuilder executarPreFilter(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (!ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoHistoricos())) {
			booleanBuilder.and(faturamentoCabecalho.codigoHistorico.in(pesquisaFaturamentoBrutoVO.getCodigoHistoricos()));
		}

		if (!ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())) {

			booleanBuilder.and(faturamentoDetalhe.codigoVeiculo.in(pesquisaFaturamentoBrutoVO.getCodigoVeiculos()));
		}

		if (pesquisaFaturamentoBrutoVO.getDataMovimentoInicial() != null || pesquisaFaturamentoBrutoVO.getDataMovimentoFinal() != null) {

			booleanBuilder
					.and(getDataMovimento(pesquisaFaturamentoBrutoVO.getDataMovimentoInicial(), pesquisaFaturamentoBrutoVO.getDataMovimentoFinal()));
		}
		return booleanBuilder;
	}

	private Predicate getDataMovimento(Date dataMovimentoInicial, Date dataMovimentoFinal) {

		if (dataMovimentoInicial != null && dataMovimentoFinal != null) {
			return faturamentoCabecalho.dataMovimento.between(dataMovimentoInicial, dataMovimentoFinal);
		}

		if (dataMovimentoInicial != null) {
			return faturamentoCabecalho.dataMovimento.goe(dataMovimentoInicial);
		}

		if (dataMovimentoFinal != null) {
			return faturamentoCabecalho.dataMovimento.loe(dataMovimentoFinal);
		}

		return null;
	}

	public List<FaturamentoVeiculoProjection> buscarFaturamentoVeiculo(Date dataMovimentoInicial, Date dataMovimentoFinal) {

		JPASQLQuery query = sqlFrom();

		query.innerJoin(faturamentoDetalhe).on(faturamentoCabecalho.idFaturamentoCabecalho.eq(faturamentoDetalhe.codigoFaturamentoCabecalho));

		Predicate predicate = getDataMovimento(dataMovimentoInicial, dataMovimentoFinal);

		if (predicate != null) {

			query.where(predicate);
		}

		query.groupBy(faturamentoDetalhe.codigoVeiculo);

		return query.list(Projections.fields(FaturamentoVeiculoProjection.class, faturamentoDetalhe.codigoVeiculo.as("codigoVeiculo"),
				faturamentoDetalhe.valorBruto.sum().as("valorBruto")));
	}

	public BigDecimal obterFaturamentoBrutoMensal(Integer mes) {

		return from().where(faturamentoCabecalho.dataMovimento.month().eq(mes)).singleResult(faturamentoCabecalho.valorBruto.sum());
	}

	public BigDecimal buscarFaturamentoVeiculoMensal(Integer mes, Long veiculo) {
		JPASQLQuery query = sqlFrom();

		return query.innerJoin(faturamentoDetalhe).on(faturamentoCabecalho.idFaturamentoCabecalho.eq(faturamentoDetalhe.codigoFaturamentoCabecalho))
				.where(faturamentoCabecalho.dataMovimento.month().eq(mes).and(faturamentoDetalhe.codigoVeiculo.eq(veiculo)))
				.singleResult(faturamentoDetalhe.valorBruto.sum());
	}

	public List<FaturamentoProjection> filtrarFaturamento(PesquisaFaturamentoVO pesquisaVO) {
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoConta())) {

			booleanBuilder.and(faturamentoCabecalho.idFaturamentoCabecalho.eq(pesquisaVO.getCodigoConta()));
		}

		if (!pesquisaVO.getCodigoFornecedores().isEmpty()) {

			booleanBuilder.and(faturamentoCabecalho.codigoCliente.in(pesquisaVO.getCodigoFornecedores()));
		}

		if (pesquisaVO.getCodigoHistoricos() != null && !pesquisaVO.getCodigoHistoricos().isEmpty()) {

			booleanBuilder.and(faturamentoCabecalho.codigoHistorico.in(pesquisaVO.getCodigoHistoricos()));
		}

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoVeiculo())) {

			booleanBuilder.and(faturamentoDetalhe.codigoVeiculo.eq(pesquisaVO.getCodigoVeiculo()).or(faturamentoDetalhe.codigoVeiculo.isNull()));
		}

		if (pesquisaVO.getDataMovimentoInicial() != null || pesquisaVO.getDataMovimentoFinal() != null) {

			booleanBuilder.and(
					getDataMovimento(faturamentoCabecalho.dataMovimento, pesquisaVO.getDataMovimentoInicial(), pesquisaVO.getDataMovimentoFinal()));
		}

		if (!BigDecimalUtil.isNullOrZero(pesquisaVO.getValorInicial()) || !BigDecimalUtil.isNullOrZero(pesquisaVO.getValorFinal())) {

			booleanBuilder.and(getValorFaturamento(pesquisaVO.getValorInicial(), pesquisaVO.getValorFinal()));
		}

		JPASQLQuery query = sqlFrom().innerJoin(faturamentoDetalhe)
				.on(faturamentoCabecalho.idFaturamentoCabecalho.eq(faturamentoDetalhe.codigoFaturamentoCabecalho)).innerJoin(cliente)
				.on(faturamentoCabecalho.codigoCliente.eq(cliente.idCliente)).innerJoin(historico)
				.on(faturamentoCabecalho.codigoHistorico.eq(historico.idHistorico)).leftJoin(veiculo)
				.on(faturamentoDetalhe.codigoVeiculo.eq(veiculo.idVeiculo));

		query.groupBy(faturamentoCabecalho.idFaturamentoCabecalho, cliente.nome, faturamentoCabecalho.dataMovimento, historico.descricao,
				veiculo.placa.coalesce("TODOS"));

		if (booleanBuilder.hasValue()) {

			query.where(booleanBuilder);
		}

		return query.list(Projections.fields(FaturamentoProjection.class, faturamentoCabecalho.idFaturamentoCabecalho.as("id"),
				cliente.nome.as("cliente"), faturamentoCabecalho.dataMovimento, veiculo.placa.coalesce("TODOS").as("veiculo"),
				historico.descricao.as("historico"), faturamentoDetalhe.valorBruto.sum().as("valorBruto")));
	}

	private Predicate getDataMovimento(TemporalExpression<Date> expression, Date dataMovimentoInicial, Date dataMovimentoFinal) {

		if (dataMovimentoInicial != null && dataMovimentoFinal != null) {
			return expression.between(dataMovimentoInicial, dataMovimentoFinal);
		}

		if (dataMovimentoInicial != null) {
			return expression.goe(dataMovimentoInicial);
		}

		if (dataMovimentoFinal != null) {
			return expression.loe(dataMovimentoFinal);
		}

		throw new SysDescException("Pelo menos uma data deve ser informada");
	}

	private Predicate getValorFaturamento(BigDecimal valorInicial, BigDecimal valorFinal) {

		if (!BigDecimalUtil.isNullOrZero(valorInicial) && !BigDecimalUtil.isNullOrZero(valorFinal)) {
			return faturamentoCabecalho.valorBruto.between(valorInicial, valorFinal);
		}

		if (!BigDecimalUtil.isNullOrZero(valorInicial)) {
			return faturamentoCabecalho.valorBruto.goe(valorInicial);
		}

		if (!BigDecimalUtil.isNullOrZero(valorFinal)) {
			return faturamentoCabecalho.valorBruto.loe(valorFinal);
		}

		throw new SysDescException("Pelo menos um valor de parcela deve ser informada");
	}

}
