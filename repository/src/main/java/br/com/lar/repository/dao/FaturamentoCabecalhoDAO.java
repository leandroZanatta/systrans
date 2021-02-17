package br.com.lar.repository.dao;

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

import br.com.lar.repository.model.FaturamentoCabecalho;
import br.com.lar.repository.projection.FaturamentoBrutoReportProjection;
import br.com.lar.repository.projection.FaturamentoVeiculoProjection;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoCabecalhoDAO extends PesquisableDAOImpl<FaturamentoCabecalho> {

	private static final long serialVersionUID = 1L;

	public FaturamentoCabecalhoDAO() {
		super(faturamentoCabecalho, faturamentoCabecalho.idFaturamentoCabecalho);
	}

	public BigDecimal buscarValorFaturamentoCaixa(Long idCaixaCabecalho) {

		return from().where(faturamentoCabecalho.caixaCabecalho.idCaixaCabecalho.eq(idCaixaCabecalho))
				.singleResult(faturamentoCabecalho.valorBruto.sum());
	}

	public List<FaturamentoBrutoReportProjection> filtrarFaturamentoBruto(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

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

		JPASQLQuery query = sqlFrom().leftJoin(faturamentoDetalhe)
				.on(faturamentoCabecalho.idFaturamentoCabecalho.eq(faturamentoDetalhe.codigoFaturamentoCabecalho))
				.leftJoin(veiculo).on(faturamentoDetalhe.codigoVeiculo.eq(veiculo.idVeiculo)).leftJoin(historico)
				.on(faturamentoCabecalho.codigoHistorico.eq(historico.idHistorico));

		if (booleanBuilder.hasValue()) {

			query.where(booleanBuilder);
		}

		query.groupBy(historico.descricao, veiculo.placa.coalesce("TODOS"));

		return query.list(Projections.fields(FaturamentoBrutoReportProjection.class, historico.descricao.as("historico"),
				veiculo.placa.coalesce("TODOS").as("veiculo"),
				faturamentoDetalhe.valorBruto.add(faturamentoDetalhe.valorAcrescimo)
						.subtract(faturamentoDetalhe.valorDesconto).sum().as("valorBruto")));
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

}
