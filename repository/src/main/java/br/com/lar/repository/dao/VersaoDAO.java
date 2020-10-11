package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QVersao.versao;

import br.com.lar.repository.model.Versao;
import br.com.sysdesc.util.dao.AbstractGenericDAO;

public class VersaoDAO extends AbstractGenericDAO<Versao> {

	private static final long serialVersionUID = 1L;

	public VersaoDAO() {
		super(versao, versao.idVersao);
	}

}
