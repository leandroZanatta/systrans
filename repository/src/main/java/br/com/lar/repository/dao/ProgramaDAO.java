package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QPrograma.programa;

import java.util.List;

import br.com.lar.repository.model.Programa;
import br.com.sysdesc.util.dao.AbstractGenericDAO;

public class ProgramaDAO extends AbstractGenericDAO<Programa> {

	private static final long serialVersionUID = 1L;

	public ProgramaDAO() {
		super(programa, programa.idPrograma);
	}

	public List<Programa> buscarRootProgramas() {
		return from().where(programa.codigoModulo.isNull()).list(programa);
	}

	public List<Programa> buscarSubMenus(Long idPrograma) {

		return from().where(programa.programaPai.idPrograma.eq(idPrograma)).list(programa);
	}
}
