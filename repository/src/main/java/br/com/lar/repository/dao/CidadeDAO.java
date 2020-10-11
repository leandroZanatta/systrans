package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QCidade.cidade;

import java.util.List;

import br.com.lar.repository.model.Cidade;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class CidadeDAO extends PesquisableDAOImpl<Cidade> {

	private static final long serialVersionUID = 1L;

	public CidadeDAO() {
		super(cidade, cidade.idCidade);
	}

	public List<Cidade> buscarCidadePorEstado(Long idEstado) {

		return from().where(cidade.codigoEstado.eq(idEstado)).list(cidade);
	}

}
