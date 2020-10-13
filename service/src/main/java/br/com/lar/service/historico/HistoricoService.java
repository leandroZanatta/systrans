package br.com.lar.service.historico;

import br.com.lar.repository.dao.HistoricoDAO;
import br.com.lar.repository.model.Historico;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.constants.MensagemConstants;
import br.com.sysdesc.util.exception.SysDescException;

public class HistoricoService extends AbstractPesquisableServiceImpl<Historico> {

	public HistoricoService() {
		super(new HistoricoDAO(), Historico::getIdHistorico);
	}

	@Override
	public void validar(Historico objetoPersistir) {

		if (LongUtil.isNullOrZero(objetoPersistir.getTipoHistorico())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_TIPO_HISTORICO);
		}

		if (StringUtil.isNullOrEmpty(objetoPersistir.getDescricao())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DESCRICAO_VALIDA);
		}

	}

}
