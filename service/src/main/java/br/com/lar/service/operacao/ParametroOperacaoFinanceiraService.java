package br.com.lar.service.operacao;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.dao.ParametroOperacaoFinanceiroDAO;
import br.com.lar.repository.model.ParametroOperacaoFinanceira;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class ParametroOperacaoFinanceiraService extends AbstractPesquisableServiceImpl<ParametroOperacaoFinanceira> {

	private ParametroOperacaoFinanceiroDAO parametroOperacaoFinanceiroDAO;

	public ParametroOperacaoFinanceiraService() {
		this(new ParametroOperacaoFinanceiroDAO());
	}

	public ParametroOperacaoFinanceiraService(ParametroOperacaoFinanceiroDAO parametroOperacaoFinanceiroDAO) {
		super(parametroOperacaoFinanceiroDAO, ParametroOperacaoFinanceira::getIdParametroOperacaoFinanceiro);

		this.parametroOperacaoFinanceiroDAO = parametroOperacaoFinanceiroDAO;
	}

	@Override
	public void validar(ParametroOperacaoFinanceira objetoPersistir) {

		if (objetoPersistir.getCodigoTipoConta() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_TIPO_CONTA);
		}
		if (objetoPersistir.getHistorico() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_HISTORICO);
		}

		if (objetoPersistir.getFormasPagamento() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_FORMA_PAGAMAMENTO);
		}

		boolean existsOperacaoFinanceira = parametroOperacaoFinanceiroDAO.existsParametroOperacao(
				objetoPersistir.getIdParametroOperacaoFinanceiro(), objetoPersistir.getHistorico().getIdHistorico(),
				objetoPersistir.getCodigoTipoConta(),
				objetoPersistir.getFormasPagamento().getIdFormaPagamento());

		if (existsOperacaoFinanceira) {

			throw new SysDescException(MensagemConstants.MENSAGEM_PARAMETRO_OPERACAO_FINANCEIRA_EXISTENTE);
		}

		if (objetoPersistir.getContaCredora() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_CONTA_CREDORA);
		}

		if (objetoPersistir.getContaDevedora() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_CONTA_DEVEDORA);
		}
	}

	public void salvar(ParametroOperacaoFinanceira objetoPersistir) {

		this.parametroOperacaoFinanceiroDAO.salvar(objetoPersistir);
	}

	public ParametroOperacaoFinanceira buscarParametroOperacao(Long codigoHistorico, Long codigoTipoConta, Long codigoFormaPagamento) {

		return parametroOperacaoFinanceiroDAO.buscarParametroOperacao(codigoHistorico, codigoTipoConta, codigoFormaPagamento);
	}

	public BooleanBuilder filtrarTipoConta(Long codigo) {

		return parametroOperacaoFinanceiroDAO.filtrarTipoConta(codigo);
	}
}
