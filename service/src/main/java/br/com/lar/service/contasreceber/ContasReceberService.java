package br.com.lar.service.contasreceber;

import java.util.List;

import br.com.lar.repository.dao.ContasReceberDAO;
import br.com.lar.repository.model.ContasReceber;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.systrans.util.vo.PesquisaContasVO;

public class ContasReceberService extends AbstractPesquisableServiceImpl<ContasReceber> {

	private ContasReceberDAO contasReceberDAO;

	public ContasReceberService() {
		this(new ContasReceberDAO());
	}

	public ContasReceberService(ContasReceberDAO contasReceberDAO) {
		super(contasReceberDAO, ContasReceber::getIdContasReceber);

		this.contasReceberDAO = contasReceberDAO;
	}

	public List<ContasReceber> filtrarContasReceber(PesquisaContasVO pesquisaContasReceberVO) {

		return contasReceberDAO.filtrarContasReceber(pesquisaContasReceberVO);
	}

}
