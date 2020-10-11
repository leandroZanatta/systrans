package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QEstado.estado;

import br.com.lar.repository.model.Estado;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class EstadoDAO extends PesquisableDAOImpl<Estado> {

	private static final long serialVersionUID = 1L;

	public EstadoDAO() {
		super(estado, estado.idEstado);
	}

}
