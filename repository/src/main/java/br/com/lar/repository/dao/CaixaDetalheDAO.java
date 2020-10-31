package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QCaixaDetalhe.caixaDetalhe;
import static br.com.lar.repository.model.QPlanoContas.planoContas;

import java.util.Collection;
import java.util.List;

import com.mysema.query.types.Projections;

import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.projection.ResumoCaixaDetalheProjection;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class CaixaDetalheDAO extends PesquisableDAOImpl<CaixaDetalhe> {

	private static final long serialVersionUID = 1L;

	public CaixaDetalheDAO() {
		super(caixaDetalhe, caixaDetalhe.idCaixaDetalhe);
	}

	public List<ResumoCaixaDetalheProjection> buscarResumoCaixa(Long idCaixaCabecalho) {

		return sqlFrom().where(caixaDetalhe.codigoCaixaCabecalho.eq(idCaixaCabecalho)).groupBy(caixaDetalhe.tipoSaldo)
				.list(Projections.fields(ResumoCaixaDetalheProjection.class,
						caixaDetalhe.tipoSaldo, caixaDetalhe.valorDetalhe.sum().as("valorSaldo")));
	}

	public Collection<ResumoCaixaDetalheProjection> buscarResumoCaixaSemValorDinheiro(Long idCaixaCabecalho, Long contaCaixa) {

		return sqlFrom().innerJoin(planoContas).on(caixaDetalhe.codigoPlanoContas.eq(planoContas.idPlanoContas))
				.where(caixaDetalhe.codigoCaixaCabecalho.eq(idCaixaCabecalho).and(planoContas.idPlanoContas.ne(contaCaixa)))
				.groupBy(caixaDetalhe.tipoSaldo)
				.list(Projections.fields(ResumoCaixaDetalheProjection.class,
						caixaDetalhe.tipoSaldo, caixaDetalhe.valorDetalhe.sum().as("valorSaldo")));
	}

	public List<CaixaDetalhe> buscarCaixaDetalhes(Long idCaixaCabecalho) {

		return from().where(caixaDetalhe.codigoCaixaCabecalho.eq(idCaixaCabecalho)).list(caixaDetalhe);
	}

}
