package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QCliente.cliente;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.Cliente;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.LongUtil;

public class ClienteDAO extends PesquisableDAOImpl<Cliente> {

	private static final long serialVersionUID = 1L;

	public ClienteDAO() {
		super(cliente, cliente.idCliente);
	}

	public Cliente buscarClientePorCpf(String cgc, Long idCliente) {

		BooleanBuilder booleanBuilder = new BooleanBuilder(cliente.cgc.eq(cgc));

		if (!LongUtil.isNullOrZero(idCliente)) {
			booleanBuilder.and(cliente.idCliente.ne(idCliente));
		}

		return from().where(booleanBuilder).singleResult(cliente);
	}

}
