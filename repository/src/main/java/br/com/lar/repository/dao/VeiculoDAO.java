package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QVeiculo.veiculo;

import br.com.lar.repository.model.Veiculo;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class VeiculoDAO extends PesquisableDAOImpl<Veiculo> {

	private static final long serialVersionUID = 1L;

	public VeiculoDAO() {
		super(veiculo, veiculo.idVeiculo);
	}

}
