package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QSalario.salario;

import java.util.List;

import br.com.lar.repository.model.Salario;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class SalarioDAO extends PesquisableDAOImpl<Salario> {

	private static final long serialVersionUID = 1L;

	public SalarioDAO() {
		super(salario, salario.idSalario);
	}

	public List<Salario> buscarSalariosPorFuncionario(Long idFuncionario) {

		return from().where(salario.funcionario.idFuncionario.eq(idFuncionario)).list(salario);
	}

}
