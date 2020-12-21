package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QContasPagarPagamento.contasPagarPagamento;

import br.com.lar.repository.model.ContasPagarPagamento;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class ContasPagarPagamentoDAO extends PesquisableDAOImpl<ContasPagarPagamento> {

	private static final long serialVersionUID = 1L;

	public ContasPagarPagamentoDAO() {
		super(contasPagarPagamento, contasPagarPagamento.idContasPagarPagamento);
	}

}
