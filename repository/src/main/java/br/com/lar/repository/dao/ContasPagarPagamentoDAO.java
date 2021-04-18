package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QContasPagar.contasPagar;
import static br.com.lar.repository.model.QContasPagarPagamento.contasPagarPagamento;
import static br.com.lar.repository.model.QContasPagarVeiculo.contasPagarVeiculo;
import static br.com.lar.repository.model.QVeiculo.veiculo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.Projections;

import br.com.lar.repository.model.ContasPagarPagamento;
import br.com.lar.repository.projection.DespesasFinanceirasProjection;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class ContasPagarPagamentoDAO extends PesquisableDAOImpl<ContasPagarPagamento> {

	private static final long serialVersionUID = 1L;

	public ContasPagarPagamentoDAO() {
		super(contasPagarPagamento, contasPagarPagamento.idContasPagarPagamento);
	}

	public List<DespesasFinanceirasProjection> filtrarDespesasFinanceiras(
			PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		return sqlFrom().innerJoin(contasPagar).on(contasPagar.idContasPagar.eq(contasPagarPagamento.codigoContasPagar))
				.leftJoin(contasPagarVeiculo).on(contasPagar.idContasPagar.eq(contasPagarVeiculo.codigoContasPagar))
				.leftJoin(veiculo).on(contasPagarVeiculo.codigoVeiculo.eq(veiculo.idVeiculo))
				.where(montarClausulasFiltroDespesasFinanceiras(pesquisaFaturamentoBrutoVO))
				.list(Projections.fields(DespesasFinanceirasProjection.class, contasPagarPagamento.valorJuros,
						contasPagarPagamento.valorAcrescimo, veiculo.placa.coalesce("TODOS").as("veiculo")));
	}

	public BigDecimal filtrarDespesasFinanceirasGeral(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		return sqlFrom().innerJoin(contasPagar).on(contasPagar.idContasPagar.eq(contasPagarPagamento.codigoContasPagar))
				.leftJoin(contasPagarVeiculo).on(contasPagar.idContasPagar.eq(contasPagarVeiculo.codigoContasPagar))
				.where(montarClausulasFiltroDespesasFinanceiras(pesquisaFaturamentoBrutoVO))
				.singleResult(contasPagarPagamento.valorJuros.add(contasPagarPagamento.valorAcrescimo).sum());
	}

	private BooleanBuilder montarClausulasFiltroDespesasFinanceiras(
			PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		BooleanBuilder booleanBuilder = new BooleanBuilder(contasPagarPagamento.valorAcrescimo.ne(BigDecimal.ZERO)
				.or(contasPagarPagamento.valorJuros.ne(BigDecimal.ZERO)));

		if (!ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())) {

			booleanBuilder.and(contasPagarVeiculo.codigoVeiculo.in(pesquisaFaturamentoBrutoVO.getCodigoVeiculos()));
		}

		if (pesquisaFaturamentoBrutoVO.getDataMovimentoInicial() != null
				|| pesquisaFaturamentoBrutoVO.getDataMovimentoFinal() != null) {

			booleanBuilder.and(getDataMovimento(pesquisaFaturamentoBrutoVO.getDataMovimentoInicial(),
					pesquisaFaturamentoBrutoVO.getDataMovimentoFinal()));
		}

		return booleanBuilder;
	}

	private Predicate getDataMovimento(Date dataMovimentoInicial, Date dataMovimentoFinal) {

		if (dataMovimentoInicial != null && dataMovimentoFinal != null) {
			return contasPagarPagamento.dataMovimento.between(dataMovimentoInicial, dataMovimentoFinal);
		}

		if (dataMovimentoInicial != null) {
			return contasPagarPagamento.dataMovimento.goe(dataMovimentoInicial);
		}

		if (dataMovimentoFinal != null) {
			return contasPagarPagamento.dataMovimento.loe(dataMovimentoFinal);
		}

		throw new SysDescException("Pelo menos uma data deve ser informada");
	}

}
