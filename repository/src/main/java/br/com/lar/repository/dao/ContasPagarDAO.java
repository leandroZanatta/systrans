package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QContasPagar.contasPagar;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.sql.JPASQLQuery;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.DateExpression;

import br.com.lar.repository.model.ContasPagar;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.sysdesc.util.vo.PesquisaContasVO;

public class ContasPagarDAO extends PesquisableDAOImpl<ContasPagar> {

	private static final long serialVersionUID = 1L;

	public ContasPagarDAO() {
		super(contasPagar, contasPagar.idContasPagar);
	}

	public List<ContasPagar> buscarTitulosAbertosCliente(Long idCliente) {

		return from().where(contasPagar.cliente.idCliente.eq(idCliente).and(contasPagar.baixado.eq(Boolean.FALSE))).list(contasPagar);
	}

	public List<ContasPagar> filtrarContasPagar(PesquisaContasVO pesquisaContasReceberVO) {

		JPASQLQuery query = sqlFrom();

		BooleanBuilder clausulas = montarClausulasFiltroContasReceber(pesquisaContasReceberVO);

		if (clausulas.hasValue()) {
			query.where(clausulas);
		}

		return query.list(contasPagar);
	}

	private BooleanBuilder montarClausulasFiltroContasReceber(PesquisaContasVO pesquisaVO) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (!StringUtil.isNullOrEmpty(pesquisaVO.getCodigoDocumento())) {
			booleanBuilder.and(contasPagar.documento.eq(pesquisaVO.getCodigoDocumento()));
		}

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoConta())) {
			booleanBuilder.and(contasPagar.idContasPagar.eq(pesquisaVO.getCodigoConta()));
		}

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoCliente())) {
			booleanBuilder.and(contasPagar.codigoCliente.eq(pesquisaVO.getCodigoCliente()));
		}

		if (!LongUtil.isNullOrZero(pesquisaVO.getCodigoFormaPagamento())) {
			booleanBuilder.and(contasPagar.codigoFormaPagamento.eq(pesquisaVO.getCodigoFormaPagamento()));
		}

		if (pesquisaVO.getDataVencimentoInicial() != null || pesquisaVO.getDataVencimentoFinal() != null) {

			booleanBuilder
					.and(getDataVencimento(pesquisaVO.getDataVencimentoInicial(), pesquisaVO.getDataVencimentoFinal()));
		}

		if (!BigDecimalUtil.isNullOrZero(pesquisaVO.getValorParcelaInicial())
				|| !BigDecimalUtil.isNullOrZero(pesquisaVO.getValorParcelaFinal())) {

			booleanBuilder.and(getValorParcela(pesquisaVO.getValorParcelaInicial(), pesquisaVO.getValorParcelaFinal()));
		}

		if (pesquisaVO.isDocumentoVencido()) {

			booleanBuilder.and(contasPagar.dataVencimento.before(DateExpression.currentDate()));
		}

		booleanBuilder.and(contasPagar.baixado.eq(pesquisaVO.isDocumentoBaixado()));

		return booleanBuilder;
	}

	private Predicate getValorParcela(BigDecimal valorParcelaInicial, BigDecimal valorParcelaFinal) {

		if (!BigDecimalUtil.isNullOrZero(valorParcelaInicial) && !BigDecimalUtil.isNullOrZero(valorParcelaFinal)) {
			return contasPagar.valorParcela.between(valorParcelaInicial, valorParcelaFinal);
		}

		if (!BigDecimalUtil.isNullOrZero(valorParcelaInicial)) {
			return contasPagar.valorParcela.goe(valorParcelaInicial);
		}

		if (!BigDecimalUtil.isNullOrZero(valorParcelaFinal)) {
			return contasPagar.valorParcela.loe(valorParcelaFinal);
		}

		throw new SysDescException("Pelo menos um valor de parcela deve ser informada");
	}

	private Predicate getDataVencimento(Date dataVencimentoInicial, Date dataVencimentoFinal) {

		if (dataVencimentoInicial != null && dataVencimentoFinal != null) {
			return contasPagar.dataVencimento.between(dataVencimentoInicial, dataVencimentoFinal);
		}

		if (dataVencimentoInicial != null) {
			return contasPagar.dataVencimento.goe(dataVencimentoInicial);
		}

		if (dataVencimentoFinal != null) {
			return contasPagar.dataVencimento.loe(dataVencimentoFinal);
		}

		throw new SysDescException("Pelo menos uma data deve ser informada");
	}

}
