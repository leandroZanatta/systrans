package br.com.lar.service.formaspagamento;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.dao.FormasPagamentoDAO;
import br.com.lar.repository.model.FormasPagamento;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class FormasPagamentoService extends AbstractPesquisableServiceImpl<FormasPagamento> {

	private FormasPagamentoDAO formasPagamentoDAO;

	public FormasPagamentoService() {
		this(new FormasPagamentoDAO());
	}

	public FormasPagamentoService(FormasPagamentoDAO formasPagamentoDAO) {
		super(formasPagamentoDAO, FormasPagamento::getIdFormaPagamento);

		this.formasPagamentoDAO = formasPagamentoDAO;
	}

	@Override
	public void validar(FormasPagamento objetoPersistir) {

		if (LongUtil.isNullOrZero(objetoPersistir.getCodigoFormaPagamento())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_FORMA_PAGAMAMENTO);
		}

		if (StringUtil.isNullOrEmpty(objetoPersistir.getDescricao())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DESCRICAO_VALIDA);
		}

		if (objetoPersistir.isFlagPermitePagamentoPrazo()) {

			if (LongUtil.isNullOrZero(objetoPersistir.getNumeroDiasPagamento())) {
				throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_NUMERO_DIAS);
			}

			if (BigDecimalUtil.isNullOrZero(objetoPersistir.getValorMinimoParcelas())) {
				throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_VALOR_PARCELAS);
			}

			if (LongUtil.isNullOrZero(objetoPersistir.getNumeroMaximoParcelas())) {
				throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_NUMERO_PARCELAS);
			}
		}
	}

	public BooleanBuilder buscarPagamentosComHistorico(Long codigoHistorico) {

		return formasPagamentoDAO.buscarPagamentosComHistorico(codigoHistorico);
	}

	public BooleanBuilder buscarPagamentosAVistaComHistorico(Long codigoHistorico) {

		return formasPagamentoDAO.buscarPagamentosAVistaComHistorico(codigoHistorico);
	}

	public BooleanBuilder pesquisarApenasAPrazo() {

		return formasPagamentoDAO.pesquisarApenasAPrazo();
	}
}
