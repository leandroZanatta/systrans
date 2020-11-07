package br.com.lar.service.historicocusto;

import br.com.lar.repository.dao.HistoricoCustoDAO;
import br.com.lar.repository.model.HistoricoCusto;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class HistoricoCustoService extends AbstractPesquisableServiceImpl<HistoricoCusto> {

	private HistoricoCustoDAO historicoCustoDAO;

	public HistoricoCustoService() {
		this(new HistoricoCustoDAO());
	}

	public HistoricoCustoService(HistoricoCustoDAO historicoCustoDAO) {
		super(historicoCustoDAO, HistoricoCusto::getIdHistoricoCusto);

		this.historicoCustoDAO = historicoCustoDAO;
	}

	@Override
	public void validar(HistoricoCusto objetoPersistir) {

		if (objetoPersistir.getHistorico() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_HISTORICO);
		}

		if (historicoCustoDAO.existeHistorico(objetoPersistir.getIdHistoricoCusto(), objetoPersistir.getHistorico())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_HISTORICO_EXISTENTE);
		}

		if (StringUtil.isNullOrEmpty(objetoPersistir.getFlagTipoCusto())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_TIPO_CUSTO);
		}

		if (LongUtil.isNullOrZero(objetoPersistir.getMesesAlocacao())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_MESES_ALOCACAO);
		}
	}

}
