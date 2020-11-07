package br.com.lar.service.faturamento;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.lar.repository.dao.FaturamentoEntradaDAO;
import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.DiarioCabecalho;
import br.com.lar.repository.model.FaturamentoEntrada;
import br.com.lar.service.caixa.CaixaService;
import br.com.lar.service.caixa.FaturamentoCaixa;
import br.com.lar.service.contasreceber.FaturamentoContasReceber;
import br.com.lar.service.diario.DiarioService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class FaturamentoEntradaService extends AbstractPesquisableServiceImpl<FaturamentoEntrada> {

	private FaturamentoEntradaDAO faturamentoEntradaDAO;
	private FaturamentoContasReceber faturamentoContasReceber = new FaturamentoContasReceber();
	private DiarioService faturamentoDiario = new DiarioService();
	private FaturamentoCaixa faturamentoCaixa = new FaturamentoCaixa();
	private CaixaService caixaService = new CaixaService();

	public FaturamentoEntradaService() {
		this(new FaturamentoEntradaDAO());
	}

	public FaturamentoEntradaService(FaturamentoEntradaDAO faturamentoEntradaDAO) {
		super(faturamentoEntradaDAO, FaturamentoEntrada::getIdFaturamentoEntrada);

		this.faturamentoEntradaDAO = faturamentoEntradaDAO;
	}

	@Override
	public void validar(FaturamentoEntrada objetoPersistir) {

		if (objetoPersistir.getHistorico() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_HISTORICO);
		}

		if (objetoPersistir.getCentroCusto() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_CENTRO_CUSTO);
		}

		if (objetoPersistir.getCaixaCabecalho() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_CAIXA_NAO_ENCONTRADO);
		}

		if (objetoPersistir.getCliente() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_FORNECEDOR);
		}

		if (ListUtil.isNullOrEmpty(objetoPersistir.getFaturamentoEntradaPagamentos())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_PAGAMENTOS);
		}

		if (objetoPersistir.getDataMovimento() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_DATA_MOVIMENTO_INVALIDA);
		}

	}

	@Override
	public void salvar(FaturamentoEntrada objetoPersistir) {

		caixaService.verificarCaixaAberto(objetoPersistir.getCaixaCabecalho());

		DiarioCabecalho diarioCabecalho = faturamentoDiario.registrarDiarioFaturamentoEntrada(objetoPersistir);

		List<ContasPagar> contasPagar = faturamentoContasReceber.registrarContasPagar(objetoPersistir);

		List<CaixaDetalhe> caixaDetalhes = faturamentoCaixa.registrarCaixaFaturamentoEntrada(objetoPersistir);

		EntityManager entityManager = faturamentoEntradaDAO.getEntityManager();

		try {

			entityManager.getTransaction().begin();

			entityManager.persist(objetoPersistir);

			entityManager.persist(diarioCabecalho);

			if (!ListUtil.isNullOrEmpty(contasPagar)) {

				contasPagar.forEach(entityManager::persist);
			}

			caixaDetalhes.forEach(entityManager::persist);

		} finally {
			entityManager.getTransaction().commit();
		}

	}
}
