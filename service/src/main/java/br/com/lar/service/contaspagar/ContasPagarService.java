package br.com.lar.service.contaspagar;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.lar.repository.dao.ContasPagarDAO;
import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.DiarioCabecalho;
import br.com.lar.service.caixa.CaixaService;
import br.com.lar.service.caixa.FaturamentoCaixa;
import br.com.lar.service.diario.DiarioService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.vo.PesquisaContasVO;

public class ContasPagarService extends AbstractPesquisableServiceImpl<ContasPagar> {

	private ContasPagarDAO contasPagarDAO;
	private DiarioService faturamentoDiario = new DiarioService();
	private FaturamentoCaixa faturamentoCaixa = new FaturamentoCaixa();
	private CaixaService caixaService = new CaixaService();

	public ContasPagarService() {
		this(new ContasPagarDAO());
	}

	public ContasPagarService(ContasPagarDAO contasPagarDAO) {
		super(contasPagarDAO, ContasPagar::getIdContasPagar);

		this.contasPagarDAO = contasPagarDAO;
	}

	@Override
	public void validar(ContasPagar objetoPersistir) {

		if (StringUtil.isNullOrEmpty(objetoPersistir.getDocumento())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_CODIGO_DOCUMENTO);
		}

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

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DATA_VENCIMENTO_CONTA);
		}

		if (BigDecimalUtil.isNullOrZero(objetoPersistir.getValorParcela())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_VALOR_PARCELA);
		}
	}

	@Override
	public void salvar(ContasPagar objetoPersistir) {
		caixaService.verificarCaixaAberto(objetoPersistir.getCaixaCabecalho());

		DiarioCabecalho diarioCabecalho = faturamentoDiario.registrarDiarioContasPagar(objetoPersistir);
		List<CaixaDetalhe> caixaDetalhes = faturamentoCaixa.registrarCaixaContasPagar(objetoPersistir);

		EntityManager entityManager = contasPagarDAO.getEntityManager();

		try {

			entityManager.getTransaction().begin();

			entityManager.persist(objetoPersistir);

			entityManager.persist(diarioCabecalho);

			caixaDetalhes.forEach(entityManager::persist);

		} finally {
			entityManager.getTransaction().commit();
		}
	}

	public List<ContasPagar> filtrarContasPagar(PesquisaContasVO pesquisaContasReceberVO) {

		return contasPagarDAO.filtrarContasPagar(pesquisaContasReceberVO);
	}

}
