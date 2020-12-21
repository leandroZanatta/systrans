package br.com.lar.service.contaspagar;

import java.util.List;

import br.com.lar.repository.dao.ContasPagarDAO;
import br.com.lar.repository.model.ContasPagar;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.systrans.util.vo.PesquisaContasVO;

public class ContasPagarService extends AbstractPesquisableServiceImpl<ContasPagar> {

	private ContasPagarDAO contasPagarDAO;

	public ContasPagarService() {
		this(new ContasPagarDAO());
	}

	public ContasPagarService(ContasPagarDAO contasPagarDAO) {
		super(contasPagarDAO, ContasPagar::getIdContasPagar);

		this.contasPagarDAO = contasPagarDAO;
	}

	public List<ContasPagar> filtrarContasPagar(PesquisaContasVO pesquisaContasReceberVO) {

		return contasPagarDAO.filtrarContasPagar(pesquisaContasReceberVO);
	}

}
