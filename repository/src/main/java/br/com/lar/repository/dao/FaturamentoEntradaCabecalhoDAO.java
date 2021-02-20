package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QAlocacaoCusto.alocacaoCusto;
import static br.com.lar.repository.model.QCentroCusto.centroCusto;
import static br.com.lar.repository.model.QCliente.cliente;
import static br.com.lar.repository.model.QFaturamentoEntradasCabecalho.faturamentoEntradasCabecalho;
import static br.com.lar.repository.model.QFaturamentoEntradasDetalhe.faturamentoEntradasDetalhe;
import static br.com.lar.repository.model.QHistorico.historico;
import static br.com.lar.repository.model.QVeiculo.veiculo;
import static br.com.lar.repository.model.QVinculoEntradaCusto.vinculoEntradaCusto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.sql.JPASQLQuery;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.Projections;
import com.mysema.query.types.expr.TemporalExpression;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;

import br.com.lar.repository.model.FaturamentoEntradasCabecalho;
import br.com.lar.repository.projection.FaturamentoBrutoReportProjection;
import br.com.lar.repository.projection.FaturamentoEntradaProjection;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoVO;

public class FaturamentoEntradaCabecalhoDAO extends PesquisableDAOImpl<FaturamentoEntradasCabecalho> {

	private static final long serialVersionUID = 1L;

	public FaturamentoEntradaCabecalhoDAO() {
		super(faturamentoEntradasCabecalho, faturamentoEntradasCabecalho.idFaturamentoEntradasCabecalho);
	}

	public List<FaturamentoEntradaProjection> filtrarFaturamento(PesquisaFaturamentoVO pesquisaVO) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoConta())) {

			booleanBuilder.and(faturamentoEntradasCabecalho.idFaturamentoEntradasCabecalho.eq(pesquisaVO.getCodigoConta()));
		}

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoFornecedor())) {

			booleanBuilder.and(faturamentoEntradasCabecalho.codigoCliente.eq(pesquisaVO.getCodigoFornecedor()));
		}

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoHistorico())) {

			booleanBuilder.and(faturamentoEntradasCabecalho.codigoHistorico.eq(pesquisaVO.getCodigoHistorico()));
		}

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoCentroCusto())) {

			booleanBuilder.and(faturamentoEntradasCabecalho.codigoCentroCusto.eq(pesquisaVO.getCodigoCentroCusto()));
		}

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoVeiculo())) {

			booleanBuilder.and(
					faturamentoEntradasDetalhe.codigoVeiculo.eq(pesquisaVO.getCodigoVeiculo()).or(faturamentoEntradasDetalhe.codigoVeiculo.isNull()));
		}

		if (pesquisaVO.getDataMovimentoInicial() != null || pesquisaVO.getDataMovimentoFinal() != null) {

			booleanBuilder
					.and(getDataMovimento(faturamentoEntradasCabecalho.dataMovimento, pesquisaVO.getDataMovimentoInicial(),
							pesquisaVO.getDataMovimentoFinal()));
		}

		if (!BigDecimalUtil.isNullOrZero(pesquisaVO.getValorInicial())
				|| !BigDecimalUtil.isNullOrZero(pesquisaVO.getValorFinal())) {

			booleanBuilder.and(getValorFaturamento(pesquisaVO.getValorInicial(), pesquisaVO.getValorFinal()));
		}

		JPASQLQuery query = sqlFrom()

				.innerJoin(faturamentoEntradasDetalhe)
				.on(faturamentoEntradasCabecalho.idFaturamentoEntradasCabecalho.eq(faturamentoEntradasDetalhe.codigoFaturamentoEntradasCabecalho))
				.innerJoin(cliente).on(faturamentoEntradasCabecalho.codigoCliente.eq(cliente.idCliente))
				.innerJoin(historico).on(faturamentoEntradasCabecalho.codigoHistorico.eq(historico.idHistorico))
				.leftJoin(veiculo).on(faturamentoEntradasDetalhe.codigoVeiculo.eq(veiculo.idVeiculo));

		query.groupBy(faturamentoEntradasCabecalho.idFaturamentoEntradasCabecalho, cliente.nome, faturamentoEntradasCabecalho.dataMovimento,
				historico.descricao,
				veiculo.placa.coalesce("TODOS"));

		if (booleanBuilder.hasValue()) {

			query.where(booleanBuilder);
		}

		return query.list(Projections.fields(FaturamentoEntradaProjection.class, faturamentoEntradasCabecalho.idFaturamentoEntradasCabecalho,
				cliente.nome.as("cliente"), faturamentoEntradasCabecalho.dataMovimento, veiculo.placa.coalesce("TODOS").as("veiculo"),
				historico.descricao.as("historico"),
				faturamentoEntradasDetalhe.valorBruto.sum().as("valorBruto")));
	}

	public List<FaturamentoBrutoReportProjection> filtrarFaturamentoBruto(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		if (pesquisaFaturamentoBrutoVO.isUsaAlocacaoCusto()) {

			return filtrarFaturamentoBrutoCentroCusto(pesquisaFaturamentoBrutoVO);
		}

		return filtrarFaturmamentoBrutoEntradas(pesquisaFaturamentoBrutoVO);
	}

	private List<FaturamentoBrutoReportProjection> filtrarFaturmamentoBrutoEntradas(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		BooleanBuilder booleanBuilder = gerarClausulaFaturamentoBruto(pesquisaFaturamentoBrutoVO);

		JPASQLQuery query = sqlFrom().leftJoin(faturamentoEntradasDetalhe)
				.on(faturamentoEntradasCabecalho.idFaturamentoEntradasCabecalho.eq(faturamentoEntradasDetalhe.codigoFaturamentoEntradasCabecalho))
				.leftJoin(veiculo).on(faturamentoEntradasDetalhe.codigoVeiculo.eq(veiculo.idVeiculo)).leftJoin(centroCusto)
				.on(faturamentoEntradasCabecalho.codigoCentroCusto.eq(centroCusto.idCentroCusto)).leftJoin(historico)
				.on(faturamentoEntradasCabecalho.codigoHistorico.eq(historico.idHistorico));

		if (booleanBuilder.hasValue()) {

			query.where(booleanBuilder);
		}

		query.groupBy(centroCusto.descricao, historico.descricao, veiculo.idVeiculo, veiculo.placa.coalesce("TODOS"));

		return query.list(Projections.fields(FaturamentoBrutoReportProjection.class, historico.descricao.as("historico"),
				centroCusto.descricao.as("centroCusto"), veiculo.idVeiculo.as("codigoVeiculo"), veiculo.placa.coalesce("TODOS").as("veiculo"),
				faturamentoEntradasDetalhe.valorBruto.add(faturamentoEntradasDetalhe.valorAcrescimo)
						.subtract(faturamentoEntradasDetalhe.valorDesconto).sum().as("valorBruto")));
	}

	private BooleanBuilder gerarClausulaFaturamentoBruto(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (!ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoHistoricos())) {
			booleanBuilder.and(faturamentoEntradasCabecalho.codigoHistorico.in(pesquisaFaturamentoBrutoVO.getCodigoHistoricos()));
		}

		if (!ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoCentroCustos())) {

			booleanBuilder.and(faturamentoEntradasCabecalho.codigoCentroCusto.in(pesquisaFaturamentoBrutoVO.getCodigoCentroCustos()));
		}

		if (!ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())) {

			booleanBuilder.and(faturamentoEntradasDetalhe.codigoVeiculo.isNull()
					.or(faturamentoEntradasDetalhe.codigoVeiculo.in(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())));
		}

		if (pesquisaFaturamentoBrutoVO.getDataMovimentoInicial() != null || pesquisaFaturamentoBrutoVO.getDataMovimentoFinal() != null) {

			booleanBuilder
					.and(getDataMovimento(faturamentoEntradasCabecalho.dataMovimento, pesquisaFaturamentoBrutoVO.getDataMovimentoInicial(),
							pesquisaFaturamentoBrutoVO.getDataMovimentoFinal()));
		}
		return booleanBuilder;
	}

	private List<FaturamentoBrutoReportProjection> filtrarFaturamentoBrutoCentroCusto(PesquisaFaturamentoBrutoVO pesquisaVO) {
		JPASQLQuery query = sqlFrom();

		NumberPath<Long> codigoDetalhe = Expressions.numberPath(Long.class, "codigoDetalhe");
		NumberPath<BigDecimal> valorParcela = Expressions.numberPath(BigDecimal.class, "valorParcela");
		NumberPath<Long> codigoCentroCusto = Expressions.numberPath(Long.class, "codigoCentroCusto");
		NumberPath<Long> codigoVeiculo = Expressions.numberPath(Long.class, "codigoVeiculo");

		EntityPathBase<Tuple> pathCustos = new EntityPathBase<>(Tuple.class, "custos");

		JPASubQuery subquery = subQuery().from(vinculoEntradaCusto);

		subquery.leftJoin(alocacaoCusto).on(vinculoEntradaCusto.codigoAlocacaoCusto.eq(alocacaoCusto.idAlocacaoCusto));

		subquery.where(getDataMovimento(alocacaoCusto.periodo, pesquisaVO.getDataMovimentoInicial(), pesquisaVO.getDataMovimentoFinal()));

		subquery.groupBy(vinculoEntradaCusto.codigoFaturamentoEntradasDetalhe, alocacaoCusto.codigoCentroCusto, alocacaoCusto.codigoVeiculo);

		query.with(pathCustos, subquery.list(vinculoEntradaCusto.codigoFaturamentoEntradasDetalhe.as(codigoDetalhe),
				alocacaoCusto.codigoCentroCusto.as(codigoCentroCusto), alocacaoCusto.codigoVeiculo.as(codigoVeiculo),
				alocacaoCusto.valorParcela.sum().as(valorParcela)));

		query.innerJoin(historico).on(faturamentoEntradasCabecalho.codigoHistorico.eq(historico.idHistorico));

		query.innerJoin(faturamentoEntradasDetalhe)
				.on(faturamentoEntradasCabecalho.idFaturamentoEntradasCabecalho.eq(faturamentoEntradasDetalhe.codigoFaturamentoEntradasCabecalho));

		query.innerJoin(pathCustos).on(faturamentoEntradasDetalhe.idFaturamentoEntradasDetalhe.eq(codigoDetalhe));

		query.leftJoin(veiculo).on(codigoVeiculo.eq(veiculo.idVeiculo));

		query.leftJoin(centroCusto).on(codigoCentroCusto.eq(centroCusto.idCentroCusto));

		query.where(gerarClausulaFaturamentoBruto(pesquisaVO));

		query.groupBy(centroCusto.descricao.coalesce("TODOS"), historico.descricao, veiculo.idVeiculo, veiculo.placa.coalesce("TODOS"));

		return query.list(Projections.fields(FaturamentoBrutoReportProjection.class, historico.descricao.as("historico"),
				centroCusto.descricao.coalesce("TODOS").as("centroCusto"), veiculo.idVeiculo.as("codigoVeiculo"),
				veiculo.placa.coalesce("TODOS").as("veiculo"),
				valorParcela.sum().as("valorBruto")));
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
			return faturamentoEntradasCabecalho.valorBruto.between(valorInicial, valorFinal);
		}

		if (!BigDecimalUtil.isNullOrZero(valorInicial)) {
			return faturamentoEntradasCabecalho.valorBruto.goe(valorInicial);
		}

		if (!BigDecimalUtil.isNullOrZero(valorFinal)) {
			return faturamentoEntradasCabecalho.valorBruto.loe(valorFinal);
		}

		throw new SysDescException("Pelo menos um valor de parcela deve ser informada");
	}
}
