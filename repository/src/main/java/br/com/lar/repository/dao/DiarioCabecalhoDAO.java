package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QDiarioCabecalho.diarioCabecalho;
import static br.com.lar.repository.model.QDiarioDetalhe.diarioDetalhe;
import static br.com.lar.repository.model.QPlanoContas.planoContas;

import java.util.List;

import com.mysema.query.types.Projections;

import br.com.lar.repository.model.DiarioCabecalho;
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

}
