package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QFaturamentoEntradasCabecalho.faturamentoEntradasCabecalho;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.types.Predicate;

import br.com.lar.repository.model.FaturamentoEntradasCabecalho;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.vo.PesquisaFaturamentoVO;

public class FaturamentoEntradaCabecalhoDAO extends PesquisableDAOImpl<FaturamentoEntradasCabecalho> {

	private static final long serialVersionUID = 1L;

	public FaturamentoEntradaCabecalhoDAO() {
		super(faturamentoEntradasCabecalho, faturamentoEntradasCabecalho.idFaturamentoEntradasCabecalho);
	}

	public List<FaturamentoEntradasCabecalho> filtrarFaturamento(PesquisaFaturamentoVO pesquisaVO) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoConta())) {

			booleanBuilder.and(faturamentoEntradasCabecalho.idFaturamentoEntradasCabecalho.eq(pesquisaVO.getCodigoConta()));
		}

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoFornecedor())) {

			booleanBuilder.and(faturamentoEntradasCabecalho.cliente.idCliente.eq(pesquisaVO.getCodigoFornecedor()));
		}

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoHistorico())) {

			booleanBuilder.and(faturamentoEntradasCabecalho.historico.idHistorico.eq(pesquisaVO.getCodigoHistorico()));
		}

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoCentroCusto())) {

			booleanBuilder.and(faturamentoEntradasCabecalho.centroCusto.idCentroCusto.eq(pesquisaVO.getCodigoCentroCusto()));
		}

		if (pesquisaVO.getDataMovimentoInicial() != null || pesquisaVO.getDataMovimentoFinal() != null) {

			booleanBuilder
					.and(getDataMovimento(pesquisaVO.getDataMovimentoInicial(), pesquisaVO.getDataMovimentoFinal()));
		}

		if (!BigDecimalUtil.isNullOrZero(pesquisaVO.getValorInicial())
				|| !BigDecimalUtil.isNullOrZero(pesquisaVO.getValorFinal())) {

			booleanBuilder.and(getValorFaturamento(pesquisaVO.getValorInicial(), pesquisaVO.getValorFinal()));
		}

		JPQLQuery query = from();

		if (booleanBuilder.hasValue()) {

			query.where(booleanBuilder);
		}

		return query.list(faturamentoEntradasCabecalho);
	}

	private Predicate getDataMovimento(Date dataMovimentoInicial, Date dataMovimentoFinal) {

		if (dataMovimentoInicial != null && dataMovimentoFinal != null) {
			return faturamentoEntradasCabecalho.dataMovimento.between(dataMovimentoInicial, dataMovimentoFinal);
		}

		if (dataMovimentoInicial != null) {
			return faturamentoEntradasCabecalho.dataMovimento.goe(dataMovimentoInicial);
		}

		if (dataMovimentoFinal != null) {
			return faturamentoEntradasCabecalho.dataMovimento.loe(dataMovimentoFinal);
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
