package br.com.lar.service.contasreceber;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.lar.repository.dao.ContasReceberDAO;
import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.model.DiarioCabecalho;
import br.com.lar.service.caixa.CaixaService;
import br.com.lar.service.caixa.FaturamentoCaixa;
import br.com.lar.service.diario.DiarioService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.vo.PesquisaContasVO;

public class ContasReceberService extends AbstractPesquisableServiceImpl<ContasReceber> {

	private ContasReceberDAO contasReceberDAO;
	private DiarioService faturamentoDiario = new DiarioService();
	private FaturamentoCaixa faturamentoCaixa = new FaturamentoCaixa();
	private CaixaService caixaService = new CaixaService();

	public ContasReceberService() {
		this(new ContasReceberDAO());
	}

	public ContasReceberService(ContasReceberDAO contasReceberDAO) {
		super(contasReceberDAO, ContasReceber::getIdContasReceber);

		this.contasReceberDAO = contasReceberDAO;
	}

	@Override
	public void validar(ContasReceber objetoPersistir) {

		if (objetoPersistir.getCaixaCabecalho() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_CAIXA_NAO_ENCONTRADO);
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

	@Override
	public void salvar(ContasReceber objetoPersistir) {
		caixaService.verificarCaixaAberto(objetoPersistir.getCaixaCabecalho());

		DiarioCabecalho diarioCabecalho = faturamentoDiario.registrarDiarioContasReceber(objetoPersistir);
		List<CaixaDetalhe> caixaDetalhes = faturamentoCaixa.registrarCaixaContasReceber(objetoPersistir);

		EntityManager entityManager = contasReceberDAO.getEntityManager();

		try {

			entityManager.getTransaction().begin();

			entityManager.persist(objetoPersistir);

			entityManager.persist(diarioCabecalho);

			caixaDetalhes.forEach(entityManager::persist);

		} finally {
			entityManager.getTransaction().commit();
		}
	}

	public List<ContasReceber> filtrarContasReceber(PesquisaContasVO pesquisaContasReceberVO) {

		return contasReceberDAO.filtrarContasReceber(pesquisaContasReceberVO);
	}

}
