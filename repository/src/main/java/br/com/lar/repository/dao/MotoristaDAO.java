package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QMotorista.motorista;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.Motorista;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.LongUtil;

public class MotoristaDAO extends PesquisableDAOImpl<Motorista> {

	private static final long serialVersionUID = 1L;

	public MotoristaDAO() {
		super(motorista, motorista.idMotorista);
	}

	public Motorista buscarMotoristaCadastrado(Long codigoFuncionario, Long idMotorista) {

		BooleanBuilder booleanBuilder = new BooleanBuilder(motorista.funcionario.idFuncionario.eq(codigoFuncionario));

		if (!LongUtil.isNullOrZero(idMotorista)) {

			booleanBuilder.and(motorista.idMotorista.ne(idMotorista));
		}

		return from().where(booleanBuilder).singleResult(motorista);
	}
}
