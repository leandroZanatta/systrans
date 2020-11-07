package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QCentroCusto.centroCusto;

import br.com.lar.repository.model.CentroCusto;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class CentroCustoDAO extends PesquisableDAOImpl<CentroCusto> {

	private static final long serialVersionUID = 1L;

	public CentroCustoDAO() {
		super(centroCusto, centroCusto.idCentroCusto);
	}

}
