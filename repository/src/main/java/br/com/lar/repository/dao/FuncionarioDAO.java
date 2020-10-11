package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QFuncionario.funcionario;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.Funcionario;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.LongUtil;

public class FuncionarioDAO extends PesquisableDAOImpl<Funcionario> {

	private static final long serialVersionUID = 1L;

	public FuncionarioDAO() {
		super(funcionario, funcionario.idFuncionario);
	}

	public Funcionario buscarFuncionarioCadastrado(Long codigoCliente, Long idFuncionario) {

		BooleanBuilder booleanBuilder = new BooleanBuilder(funcionario.cliente.idCliente.eq(codigoCliente));

		if (!LongUtil.isNullOrZero(idFuncionario)) {

			booleanBuilder.and(funcionario.idFuncionario.ne(idFuncionario));
		}

		return from().where(booleanBuilder).singleResult(funcionario);
	}

}
