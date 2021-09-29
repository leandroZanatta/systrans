package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QAlocacaoCusto.alocacaoCusto;
import static br.com.lar.repository.model.QFaturamentoEntradasCabecalho.faturamentoEntradasCabecalho;
import static br.com.lar.repository.model.QFaturamentoEntradasDetalhe.faturamentoEntradasDetalhe;
import static br.com.lar.repository.model.QHistorico.historico;
import static br.com.lar.repository.model.QHistoricoCusto.historicoCusto;
import static br.com.lar.repository.model.QVeiculo.veiculo;
import static br.com.lar.repository.model.QVinculoEntradaCusto.vinculoEntradaCusto;

import java.util.Date;
import java.util.List;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.sql.JPASQLQuery;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.Projections;

import br.com.lar.repository.model.AlocacaoCusto;
import br.com.lar.repository.projection.AlocacaoCustoProjection;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.systrans.util.vo.PesquisaCentroCustoVO;

public class AlocacaoCustoDAO extends PesquisableDAOImpl<AlocacaoCusto> {

	private static final long serialVersionUID = 1L;

	public AlocacaoCustoDAO() {
		super(alocacaoCusto, alocacaoCusto.idAlocacaoCusto);
	}

	public List<AlocacaoCustoProjection> filtrarAlocacaoCusto(PesquisaCentroCustoVO pesquisaCentroCustoVO) {
		BooleanBuilder booleanBuilder = executarPreFilter(pesquisaCentroCustoVO);

		JPASQLQuery query = sqlQuery().from(faturamentoEntradasCabecalho);

		query.innerJoin(faturamentoEntradasDetalhe)
				.on(faturamentoEntradasCabecalho.idFaturamentoEntradasCabecalho.eq(faturamentoEntradasDetalhe.codigoFaturamentoEntradasCabecalho));

		query.innerJoin(vinculoEntradaCusto)
				.on(vinculoEntradaCusto.codigoFaturamentoEntradasDetalhe.eq(faturamentoEntradasDetalhe.idFaturamentoEntradasDetalhe));

		query.innerJoin(alocacaoCusto).on(vinculoEntradaCusto.codigoAlocacaoCusto.eq(alocacaoCusto.idAlocacaoCusto));

		query.leftJoin(veiculo).on(alocacaoCusto.codigoVeiculo.eq(veiculo.idVeiculo));

		query.leftJoin(historicoCusto).on(alocacaoCusto.codigohistoricoCusto.eq(historicoCusto.idHistoricoCusto));

		query.leftJoin(historico).on(historicoCusto.codigoHistorico.eq(historico.idHistorico));

		if (booleanBuilder.hasValue()) {
			query.where(booleanBuilder);
		}

		return query.list(Projections.fields(AlocacaoCustoProjection.class, alocacaoCusto.valorParcela.as("valorParcela"),
				historico.descricao.as("historico"), veiculo.placa.coalesce("TODOS").as("placa"),
				faturamentoEntradasCabecalho.idFaturamentoEntradasCabecalho.as("codigo"), alocacaoCusto.periodo.month().as("periodo")));
	}

	private BooleanBuilder executarPreFilter(PesquisaCentroCustoVO pesquisaCentroCustoVO) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (!ListUtil.isNullOrEmpty(pesquisaCentroCustoVO.getCodigoHistoricos())) {
			booleanBuilder.and(historicoCusto.codigoHistorico.in(pesquisaCentroCustoVO.getCodigoHistoricos()));
		}

		if (!ListUtil.isNullOrEmpty(pesquisaCentroCustoVO.getCodigoVeiculos())) {

			booleanBuilder.and(alocacaoCusto.codigoVeiculo.in(pesquisaCentroCustoVO.getCodigoVeiculos()));
		}

		if (!ListUtil.isNullOrEmpty(pesquisaCentroCustoVO.getCodigoCentroCustos())) {

			booleanBuilder.and(alocacaoCusto.codigoCentroCusto.in(pesquisaCentroCustoVO.getCodigoCentroCustos()));
		}

		if (!ListUtil.isNullOrEmpty(pesquisaCentroCustoVO.getDespesas())) {

			booleanBuilder.and(faturamentoEntradasCabecalho.idFaturamentoEntradasCabecalho.in(pesquisaCentroCustoVO.getDespesas()));
		}

		if (pesquisaCentroCustoVO.getDataMovimentoInicial() != null || pesquisaCentroCustoVO.getDataMovimentoFinal() != null) {

			booleanBuilder.and(getDataMovimento(pesquisaCentroCustoVO.getDataMovimentoInicial(), pesquisaCentroCustoVO.getDataMovimentoFinal()));
		}
		return booleanBuilder;
	}

	private Predicate getDataMovimento(Date dataMovimentoInicial, Date dataMovimentoFinal) {

		if (dataMovimentoInicial != null && dataMovimentoFinal != null) {
			return alocacaoCusto.periodo.between(dataMovimentoInicial, dataMovimentoFinal);
		}

		if (dataMovimentoInicial != null) {
			return alocacaoCusto.periodo.goe(dataMovimentoInicial);
		}

		if (dataMovimentoFinal != null) {
			return alocacaoCusto.periodo.loe(dataMovimentoFinal);
		}

		return null;
	}

}
