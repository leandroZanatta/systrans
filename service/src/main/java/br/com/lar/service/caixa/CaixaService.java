package br.com.lar.service.caixa;

import br.com.lar.repository.dao.CaixaCabecalhoDAO;
import br.com.lar.repository.dao.CaixaDAO;
import br.com.lar.repository.model.Caixa;
import br.com.lar.repository.model.CaixaCabecalho;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.constants.MensagemUtilConstants;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class CaixaService extends AbstractPesquisableServiceImpl<Caixa> {

	private CaixaDAO caixaDAO;
	private CaixaCabecalhoDAO caixaCabecalhoDAO = new CaixaCabecalhoDAO();

	public CaixaService() {
		this(new CaixaDAO());
	}

	public CaixaService(CaixaDAO caixaDAO) {
		super(caixaDAO, Caixa::getIdCaixa);

		this.caixaDAO = caixaDAO;
	}

	@Override
	public void validar(Caixa objetoPersistir) {

		if (objetoPersistir.getUsuario() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_USUARIO);
		}

		if (StringUtil.isNullOrEmpty(objetoPersistir.getDescricao())) {

			throw new SysDescException(MensagemUtilConstants.MENSAGEM_INSIRA_DESCRICAO_VALIDA);
		}

		if (caixaDAO.existeCaixaUsuario(objetoPersistir)) {
			throw new SysDescException(MensagemConstants.MENSAGEM_USUARIO_CAIXA_CADASTRADO);
		}
	}

	public void verificarCaixaAberto(CaixaCabecalho caixaCabecalho) {

		if (caixaCabecalho == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_CAIXA_NAO_ENCONTRADO);
		}

		if (!caixaCabecalhoDAO.isCaixaAberto(caixaCabecalho.getIdCaixaCabecalho())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_CAIXA_FECHADO);
		}
	}

}
