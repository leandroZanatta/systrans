package br.com.lar.service.alocacaocusto;

import java.util.List;

import br.com.lar.repository.dao.AlocacaoCustoDAO;
import br.com.lar.repository.model.AlocacaoCusto;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.systrans.util.vo.AlocacaoCustoVO;
import br.com.systrans.util.vo.PesquisaCentroCustoVO;

public class AlocacaoCustoService extends AbstractPesquisableServiceImpl<AlocacaoCusto> {

	private AlocacaoCustoDAO alocacaoCustoDAO;

	public AlocacaoCustoService() {
		this(new AlocacaoCustoDAO());
	}

	public AlocacaoCustoService(AlocacaoCustoDAO alocacaoCustoDAO) {
		super(alocacaoCustoDAO, AlocacaoCusto::getIdAlocacaoCusto);

		this.alocacaoCustoDAO = alocacaoCustoDAO;
	}

	public List<AlocacaoCustoVO> filtrarAlocacaoCusto(PesquisaCentroCustoVO pesquisaCentroCustoVO) {

		List<AlocacaoCusto> alocacaoCustos = alocacaoCustoDAO.filtrarAlocacaoCusto(pesquisaCentroCustoVO);
		return null;
	}

}
