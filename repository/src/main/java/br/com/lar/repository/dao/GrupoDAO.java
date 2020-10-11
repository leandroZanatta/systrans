package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QGrupo.grupo;

import br.com.lar.repository.model.Grupo;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class GrupoDAO extends PesquisableDAOImpl<Grupo> {

	private static final long serialVersionUID = 1L;

	public GrupoDAO() {
		super(grupo, grupo.idGrupo);
	}

}
