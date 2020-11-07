package br.com.lar.service.centrocusto;

import br.com.lar.repository.dao.CentroCustoDAO;
import br.com.lar.repository.model.CentroCusto;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class CentroCustoService extends AbstractPesquisableServiceImpl<CentroCusto> {

	public CentroCustoService() {
		super(new CentroCustoDAO(), CentroCusto::getIdCentroCusto);
	}

	@Override
	public void validar(CentroCusto objetoPersistir) {

		if (StringUtil.isNullOrEmpty(objetoPersistir.getDescricao())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DESCRICAO_VALIDA);
		}
	}
}
