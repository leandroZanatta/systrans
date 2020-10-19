package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QCaixaCabecalho.caixaCabecalho;

import br.com.lar.repository.model.CaixaCabecalho;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class CaixaCabecalhoDAO extends PesquisableDAOImpl<CaixaCabecalho> {

	private static final long serialVersionUID = 1L;

	public CaixaCabecalhoDAO() {
		super(caixaCabecalho, caixaCabecalho.idCaixaCabecalho);
	}

	public Boolean buscarCaixasAbertos(Long idUsuario) {

		return from().where(caixaCabecalho.caixa.codigoUsuario.eq(idUsuario).and(caixaCabecalho.dataFechamento.isNull())).exists();
	}

}
