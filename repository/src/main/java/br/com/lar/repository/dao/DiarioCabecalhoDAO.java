package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QDiarioCabecalho.diarioCabecalho;

import br.com.lar.repository.model.DiarioCabecalho;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class DiarioCabecalhoDAO extends PesquisableDAOImpl<DiarioCabecalho> {

	private static final long serialVersionUID = 1L;

	public DiarioCabecalhoDAO() {
		super(diarioCabecalho, diarioCabecalho.idDiarioCabecalho);
	}

}
