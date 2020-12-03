package br.com.lar.service.faturamento;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.persistence.EntityManager;

import br.com.lar.repository.dao.FaturamentoEntradaCabecalhoDAO;
import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.DiarioCabecalho;
import br.com.lar.repository.model.FaturamentoEntradaPagamentos;
import br.com.lar.repository.model.FaturamentoEntradasCabecalho;
import br.com.lar.repository.model.VinculoEntrada;
import br.com.lar.repository.model.VinculoEntradaCaixa;
import br.com.lar.repository.model.VinculoEntradaContasPagar;
import br.com.lar.service.caixa.CaixaService;
import br.com.lar.service.caixa.FaturamentoCaixa;
import br.com.lar.service.contasreceber.FaturamentoContasReceber;
import br.com.lar.service.diario.DiarioService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class FaturamentoEntradaService extends AbstractPesquisableServiceImpl<FaturamentoEntradasCabecalho> {

	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO;
	private FaturamentoContasReceber faturamentoContasReceber = new FaturamentoContasReceber();
	private DiarioService faturamentoDiario = new DiarioService();
	private FaturamentoCaixa faturamentoCaixa = new FaturamentoCaixa();
	private CaixaService caixaService = new CaixaService();

	public FaturamentoEntradaService() {
		this(new FaturamentoEntradaCabecalhoDAO());
	}

	public FaturamentoEntradaService(FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO) {
		super(faturamentoEntradaDAO, FaturamentoEntradasCabecalho::getIdFaturamentoEntradasCabecalho);

		this.faturamentoEntradaDAO = faturamentoEntradaDAO;
	}

	@Override
	public void validar(FaturamentoEntradasCabecalho objetoPersistir) {

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

		BigDecimal valorPagamentos = objetoPersistir.getFaturamentoEntradaPagamentos().stream().map(FaturamentoEntradaPagamentos::getValorParcela)
				.reduce(BigDecimal.ZERO,
						BigDecimal::add);

		if (valorPagamentos.compareTo(objetoPersistir.getValorBruto()) != 0) {

			DecimalFormat decimalFormat = new DecimalFormat("0.00");

			throw new SysDescException(MensagemConstants.MENSAGEM_DIVERGENCIA_VALORES_PAGAMENTO,
					decimalFormat.format(objetoPersistir.getValorBruto().doubleValue()),
					decimalFormat.format(valorPagamentos.doubleValue()),
					decimalFormat.format(objetoPersistir.getValorBruto().subtract(valorPagamentos).doubleValue()));
		}

	}

	@Override
	public void salvar(FaturamentoEntradasCabecalho objetoPersistir) {

		caixaService.verificarCaixaAberto(objetoPersistir.getCaixaCabecalho());

		DiarioCabecalho diarioCabecalho = faturamentoDiario.registrarDiarioFaturamentoEntrada(objetoPersistir);

		List<ContasPagar> contasPagar = faturamentoContasReceber.registrarContasPagar(objetoPersistir,
				objetoPersistir.getFaturamentoEntradasDetalhes());

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

			VinculoEntrada vinculoEntrada = new VinculoEntrada();
			vinculoEntrada.setDiarioCabecalho(diarioCabecalho);
			vinculoEntrada.setFaturamentoEntrada(objetoPersistir);
			entityManager.persist(vinculoEntrada);

			caixaDetalhes.stream().map(detalhe -> {
				VinculoEntradaCaixa vinculoEntradaCaixa = new VinculoEntradaCaixa();
				vinculoEntradaCaixa.setCaixaDetalhe(detalhe);
				vinculoEntradaCaixa.setFaturamentoEntrada(objetoPersistir);

				return vinculoEntradaCaixa;
			}).forEach(entityManager::persist);

			if (!ListUtil.isNullOrEmpty(contasPagar)) {

				contasPagar.stream().map(detalhe -> {
					VinculoEntradaContasPagar vinculoEntradaContasPagar = new VinculoEntradaContasPagar();
					vinculoEntradaContasPagar.setContasPagar(detalhe);
					vinculoEntradaContasPagar.setFaturamentoEntrada(objetoPersistir);
					return vinculoEntradaContasPagar;
				}).forEach(entityManager::persist);
			}
		} finally {
			entityManager.getTransaction().commit();
		}
	}
}
