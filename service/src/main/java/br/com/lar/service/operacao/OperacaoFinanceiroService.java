package br.com.lar.service.operacao;

import br.com.lar.repository.dao.OperacaoFinanceiroDAO;
import br.com.lar.repository.model.OperacaoFinanceiro;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class OperacaoFinanceiroService extends AbstractPesquisableServiceImpl<OperacaoFinanceiro> {

	private OperacaoFinanceiroDAO operacaoFinanceiroDAO;

	public OperacaoFinanceiroService() {
		this(new OperacaoFinanceiroDAO());
	}

	public OperacaoFinanceiroService(OperacaoFinanceiroDAO operacaoFinanceiroDAO) {
		super(operacaoFinanceiroDAO, OperacaoFinanceiro::getIdOperacaoFinanceiro);

		this.operacaoFinanceiroDAO = operacaoFinanceiroDAO;
	}

	@Override
	public void validar(OperacaoFinanceiro objetoPersistir) {

		if (objetoPersistir.getHistorico() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_HISTORICO);
		}

		if (objetoPersistir.getFormasPagamento() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_FORMA_PAGAMAMENTO);
		}

		boolean operacaoExiste = operacaoFinanceiroDAO.validarBuscarOperacao(objetoPersistir);

		if (operacaoExiste) {
			throw new SysDescException(MensagemConstants.MENSAGEM_OPERACAO_CADASTRADA);
		}

		if (objetoPersistir.getContaCredora() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_CONTA_CREDORA);
		}

		if (objetoPersistir.getContaDevedora() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_CONTA_DEVEDORA);
		}
	}

}
