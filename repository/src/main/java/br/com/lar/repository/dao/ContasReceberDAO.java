package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QContasReceber.contasReceber;
import static br.com.lar.repository.model.QContasReceberVeiculo.contasReceberVeiculo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.sql.JPASQLQuery;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.DateExpression;

import br.com.lar.repository.model.ContasReceber;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.vo.PesquisaContasVO;

public class ContasReceberDAO extends PesquisableDAOImpl<ContasReceber> {

	private static final long serialVersionUID = 1L;

	public ContasReceberDAO() {
		super(contasReceber, contasReceber.idContasReceber);
	}

	public List<ContasReceber> buscarTitulosAbertosCliente(Long idCliente) {

		return from().where(contasReceber.cliente.idCliente.eq(idCliente).and(contasReceber.baixado.eq(Boolean.FALSE))).list(contasReceber);
	}

	public List<ContasReceber> filtrarContasReceber(PesquisaContasVO pesquisaContasReceberVO) {

		JPASQLQuery query = sqlFrom();

		BooleanBuilder clausulas = montarClausulasFiltroContasReceber(pesquisaContasReceberVO);

		if (clausulas.hasValue()) {
			query.where(clausulas);
		}

		return query.orderBy(contasReceber.dataVencimento.asc()).list(contasReceber);
	}

	private BooleanBuilder montarClausulasFiltroContasReceber(PesquisaContasVO pesquisaContasReceberVO) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (!StringUtil.isNullOrEmpty(pesquisaContasReceberVO.getCodigoDocumento())) {

			booleanBuilder.and(subQuery().from(contasReceberVeiculo).where(contasReceber.idContasReceber.eq(contasReceberVeiculo.codigoContasReceber)
					.and(contasReceberVeiculo.documento.eq(pesquisaContasReceberVO.getCodigoDocumento()))).exists());
		}

		if (!LongUtil.isNullOrZero(pesquisaContasReceberVO.getCodigoConta())) {
			booleanBuilder.and(contasReceber.idContasReceber.eq(pesquisaContasReceberVO.getCodigoConta()));
		}

		if (!LongUtil.isNullOrZero(pesquisaContasReceberVO.getCodigoCliente())) {
			booleanBuilder.and(contasReceber.codigoCliente.eq(pesquisaContasReceberVO.getCodigoCliente()));
		}

		if (!LongUtil.isNullOrZero(pesquisaContasReceberVO.getCodigoFormaPagamento())) {
			booleanBuilder.and(contasReceber.codigoFormaPagamento.eq(pesquisaContasReceberVO.getCodigoFormaPagamento()));
		}

		if (pesquisaContasReceberVO.getDataVencimentoInicial() != null || pesquisaContasReceberVO.getDataVencimentoFinal() != null) {

			booleanBuilder
					.and(getDataVencimento(pesquisaContasReceberVO.getDataVencimentoInicial(), pesquisaContasReceberVO.getDataVencimentoFinal()));
		}

		if (!BigDecimalUtil.isNullOrZero(pesquisaContasReceberVO.getValorParcelaInicial())
				|| !BigDecimalUtil.isNullOrZero(pesquisaContasReceberVO.getValorParcelaFinal())) {

			booleanBuilder.and(getValorParcela(pesquisaContasReceberVO.getValorParcelaInicial(), pesquisaContasReceberVO.getValorParcelaFinal()));
		}

		if (pesquisaContasReceberVO.isDocumentoVencido()) {

			booleanBuilder.and(contasReceber.dataVencimento.before(DateExpression.currentDate()));
		}

		booleanBuilder.and(contasReceber.baixado.eq(pesquisaContasReceberVO.isDocumentoBaixado()));

		return booleanBuilder;
	}

	private Predicate getValorParcela(BigDecimal valorParcelaInicial, BigDecimal valorParcelaFinal) {

		if (!BigDecimalUtil.isNullOrZero(valorParcelaInicial) && !BigDecimalUtil.isNullOrZero(valorParcelaFinal)) {
			return contasReceber.valorParcela.between(valorParcelaInicial, valorParcelaFinal);
		}

		if (!BigDecimalUtil.isNullOrZero(valorParcelaInicial)) {
			return contasReceber.valorParcela.goe(valorParcelaInicial);
		}

		if (!BigDecimalUtil.isNullOrZero(valorParcelaFinal)) {
			return contasReceber.valorParcela.loe(valorParcelaFinal);
		}

		throw new SysDescException("Pelo menos um valor de parcela deve ser informada");
	}

	private Predicate getDataVencimento(Date dataVencimentoInicial, Date dataVencimentoFinal) {

		if (dataVencimentoInicial != null && dataVencimentoFinal != null) {
			return contasReceber.dataVencimento.between(dataVencimentoInicial, dataVencimentoFinal);
		}

		if (dataVencimentoInicial != null) {
			return contasReceber.dataVencimento.goe(dataVencimentoInicial);
		}

		if (dataVencimentoFinal != null) {
			return contasReceber.dataVencimento.loe(dataVencimentoFinal);
		}

		throw new SysDescException("Pelo menos uma data deve ser informada");
	}

}
