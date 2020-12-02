package br.com.lar.service.operacao;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.dao.OperacaoDAO;
import br.com.lar.repository.model.Operacao;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class OperacaoService extends AbstractPesquisableServiceImpl<Operacao> {

	private OperacaoDAO operacaoDAO;

	public OperacaoService() {
		this(new OperacaoDAO());
	}

	public OperacaoService(OperacaoDAO operacaoDAO) {
		super(operacaoDAO, Operacao::getIdOperacao);

		this.operacaoDAO = operacaoDAO;
	}

	@Override
	public void validar(Operacao objetoPersistir) {

		if (objetoPersistir.getHistorico() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_HISTORICO);
		}

		if (objetoPersistir.getFormasPagamento() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_FORMA_PAGAMAMENTO);
		}

		boolean operacaoExiste = operacaoDAO.validarBuscarOperacao(objetoPersistir);

		if (operacaoExiste) {
			throw new SysDescException(MensagemConstants.MENSAGEM_OPERACAO_CADASTRADA);
		}

		if (StringUtil.isNullOrEmpty(objetoPersistir.getDescricao())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DESCRICAO_VALIDA);
		}

		if (objetoPersistir.getContaCredora() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_CONTA_CREDORA);
		}

		if (objetoPersistir.getContaDevedora() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_CONTA_DEVEDORA);
		}
	}

	public BooleanBuilder buscarOperacaoContasPagar(Long codigoConta) {

		return operacaoDAO.buscarOperacaoContasPagar(codigoConta);
	}
}
