package br.com.lar.service.salario;

import java.util.List;

import br.com.lar.repository.dao.SalarioDAO;
import br.com.lar.repository.model.Salario;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;

public class SalarioService extends AbstractPesquisableServiceImpl<Salario> {

	private SalarioDAO salarioDAO;

	public SalarioService() {
		this(new SalarioDAO());
	}

	public SalarioService(SalarioDAO salarioDAO) {
		super(new SalarioDAO(), Salario::getIdSalario);

		this.salarioDAO = salarioDAO;
	}

	public List<Salario> getSalarios(Long idFuncionario) {

		return salarioDAO.buscarSalariosPorFuncionario(idFuncionario);
	}
}
