package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QFaturamentoEntrada.faturamentoEntrada;

import br.com.lar.repository.model.FaturamentoEntrada;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class FaturamentoEntradaDAO extends PesquisableDAOImpl<FaturamentoEntrada> {

	private static final long serialVersionUID = 1L;

	public FaturamentoEntradaDAO() {
		super(faturamentoEntrada, faturamentoEntrada.idFaturamentoEntrada);
	}

}
