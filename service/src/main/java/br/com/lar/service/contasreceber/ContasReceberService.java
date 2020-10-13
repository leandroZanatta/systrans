package br.com.lar.service.contasreceber;

import java.util.List;

import br.com.lar.repository.dao.ContasReceberDAO;
import br.com.lar.repository.model.ContasReceber;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.constants.MensagemConstants;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.sysdesc.util.vo.PesquisaContasReceberVO;

public class ContasReceberService extends AbstractPesquisableServiceImpl<ContasReceber> {

	private ContasReceberDAO contasReceberDAO;

	public ContasReceberService() {
		this(new ContasReceberDAO());
	}

	public ContasReceberService(ContasReceberDAO contasReceberDAO) {
		super(contasReceberDAO, ContasReceber::getIdContasReceber);

		this.contasReceberDAO = contasReceberDAO;
	}

	@Override
	public void validar(ContasReceber objetoPersistir) {

		if (StringUtil.isNullOrEmpty(objetoPersistir.getDocumento())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_CODIGO_DOCUMENTO);
		}

		if (objetoPersistir.getCliente() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_FORNECEDOR);
		}

		if (objetoPersistir.getFormasPagamento() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_FORMA_PAGAMAMENTO);
		}

		if (objetoPersistir.getHistorico() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_HISTORICO);
		}

		if (objetoPersistir.getDataMovimento() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DATA_MOVIMENTO);
		}

		if (objetoPersistir.getDataVencimento() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DATA_VENCIMENTO);
		}

		if (BigDecimalUtil.isNullOrZero(objetoPersistir.getValorParcela())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_VALOR_PARCELA);
		}
	}

	public List<ContasReceber> filtrarContasReceber(PesquisaContasReceberVO pesquisaContasReceberVO) {

		return contasReceberDAO.filtrarContasReceber(pesquisaContasReceberVO);
	}

}
