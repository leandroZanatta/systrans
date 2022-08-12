package br.com.lar.service.faturamento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import br.com.lar.repository.dao.FaturamentoCabecalhoDAO;
import br.com.lar.repository.dao.FaturamentoEntradaCabecalhoDAO;
import br.com.lar.repository.dao.HistoricoCustoDAO;
import br.com.lar.repository.dao.OperacaoDAO;
import br.com.lar.repository.model.AlocacaoCusto;
import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.ContasPagarVeiculo;
import br.com.lar.repository.model.DiarioCabecalho;
import br.com.lar.repository.model.DiarioDetalhe;
import br.com.lar.repository.model.FaturamentoEntradaPagamentos;
import br.com.lar.repository.model.FaturamentoEntradasCabecalho;
import br.com.lar.repository.model.FaturamentoEntradasDetalhe;
import br.com.lar.repository.model.HistoricoCusto;
import br.com.lar.repository.model.Operacao;
import br.com.lar.repository.model.Veiculo;
import br.com.lar.repository.model.VinculoEntrada;
import br.com.lar.repository.model.VinculoEntradaCaixa;
import br.com.lar.repository.model.VinculoEntradaContasPagar;
import br.com.lar.repository.model.VinculoEntradaContasPagarVeiculo;
import br.com.lar.repository.model.VinculoEntradaCusto;
import br.com.lar.repository.projection.FaturamentoProjection;
import br.com.lar.repository.projection.FaturamentoVeiculoProjection;
import br.com.lar.service.caixa.CaixaService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.DateUtil;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.enumeradores.TipoStatusEnum;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.RateioUtil;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum;
import br.com.systrans.util.vo.PesquisaFaturamentoVO;

public class FaturamentoEntradaService extends AbstractPesquisableServiceImpl<FaturamentoEntradasCabecalho> {

	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO;
	private CaixaService caixaService = new CaixaService();
	private OperacaoDAO operacaoDAO = new OperacaoDAO();
	private HistoricoCustoDAO historicoCustoDAO = new HistoricoCustoDAO();
	private FaturamentoCabecalhoDAO faturamentoCabecalhoDAO = new FaturamentoCabecalhoDAO();

	public FaturamentoEntradaService() {
		this(new FaturamentoEntradaCabecalhoDAO());
	}

	public FaturamentoEntradaService(FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO) {
		super(faturamentoEntradaDAO, FaturamentoEntradasCabecalho::getIdFaturamentoEntradasCabecalho);

		this.faturamentoEntradaDAO = faturamentoEntradaDAO;
	}

	@Override
	public void salvar(FaturamentoEntradasCabecalho objetoPersistir) {
		EntityManager entityManager = this.faturamentoEntradaDAO.getEntityManager();

		try {
			entityManager.getTransaction().begin();

			integrarFaturamento(entityManager, objetoPersistir);
		} finally {
			entityManager.getTransaction().commit();
		}
	}

	public void integrarFaturamento(EntityManager entityManager, FaturamentoEntradasCabecalho objetoPersistir) {

		caixaService.verificarCaixaAberto(objetoPersistir.getCaixaCabecalho());

		DiarioCabecalho diarioCabecalho = new DiarioCabecalho();
		diarioCabecalho.setDataMovimento(new Date());
		diarioCabecalho.setHistorico(objetoPersistir.getHistorico());
		diarioCabecalho.setDiarioDetalhes(new ArrayList<>());
		diarioCabecalho.setCaixaCabecalho(objetoPersistir.getCaixaCabecalho());

		List<ContasPagar> contasPagars = new ArrayList<>();
		List<CaixaDetalhe> caixaDetalhes = new ArrayList<>();
		List<AlocacaoCusto> alocacaoCustos = new ArrayList<>();

		HistoricoCusto historicoCusto = historicoCustoDAO.buscarHistorico(objetoPersistir.getHistorico().getIdHistorico());

		objetoPersistir.getFaturamentoEntradasDetalhes().forEach(detalhe -> {

			List<AlocacaoCusto> alocacaoDetalhe = new ArrayList<>();

			BigDecimal valorLiquido = detalhe.getValorBruto().subtract(detalhe.getValorDesconto()).add(detalhe.getValorAcrescimo());
			BigDecimal valorParcela = valorLiquido.divide(BigDecimal.valueOf(historicoCusto.getMesesAlocacao()), 2, RoundingMode.HALF_EVEN);
			Date periodo = objetoPersistir.getDataMovimento();

			for (int mes = 1; mes <= historicoCusto.getMesesAlocacao(); mes++) {

				AlocacaoCusto alocacaoCusto = new AlocacaoCusto();
				alocacaoCusto.setCentroCusto(objetoPersistir.getCentroCusto());
				alocacaoCusto.setHistoricoCusto(historicoCusto);
				alocacaoCusto.setMotorista(detalhe.getMotorista());
				alocacaoCusto.setNumeroParcela(Long.valueOf(mes));
				alocacaoCusto.setPeriodo(periodo);
				alocacaoCusto.setValorParcela(valorParcela);
				alocacaoCusto.setVeiculo(detalhe.getVeiculo());

				alocacaoDetalhe.add(alocacaoCusto);

				VinculoEntradaCusto vinculo = new VinculoEntradaCusto();
				vinculo.setAlocacaoCusto(alocacaoCusto);
				vinculo.setFaturamentoEntradasDetalhe(detalhe);

				detalhe.getVinculoEntradaCustos().add(vinculo);

				periodo = DateUtil.addMonth(periodo, 1L);
			}

			RateioUtil.efetuarRateio(alocacaoDetalhe, AlocacaoCusto::getValorParcela, AlocacaoCusto::setValorParcela, valorLiquido);

			alocacaoCustos.addAll(alocacaoDetalhe);
		});

		objetoPersistir.getFaturamentoEntradaPagamentos().forEach(pagamento -> {

			Operacao operacao = operacaoDAO.buscarOperacao(objetoPersistir.getHistorico().getIdHistorico(),
					pagamento.getFormasPagamento().getIdFormaPagamento());

			DiarioDetalhe diarioCredito = new DiarioDetalhe();
			diarioCredito.setDiarioCabecalho(diarioCabecalho);
			diarioCredito.setPlanoContas(operacao.getContaCredora());
			diarioCredito.setTipoSaldo(TipoHistoricoOperacaoEnum.CREDOR.getCodigo());
			diarioCredito.setValorDetalhe(pagamento.getValorParcela());

			DiarioDetalhe diarioDebito = new DiarioDetalhe();
			diarioDebito.setDiarioCabecalho(diarioCabecalho);
			diarioDebito.setPlanoContas(operacao.getContaDevedora());
			diarioDebito.setTipoSaldo(TipoHistoricoOperacaoEnum.DEVEDOR.getCodigo());
			diarioDebito.setValorDetalhe(pagamento.getValorParcela());

			diarioCabecalho.getDiarioDetalhes().add(diarioDebito);
			diarioCabecalho.getDiarioDetalhes().add(diarioCredito);

			CaixaDetalhe caixaDetalhe = new CaixaDetalhe();
			caixaDetalhe.setCaixaCabecalho(objetoPersistir.getCaixaCabecalho());
			caixaDetalhe.setDataMovimento(new Date());
			caixaDetalhe.setTipoSaldo(operacao.getHistorico().getTipoHistorico());
			caixaDetalhe.setPlanoContas(operacao.getContaCredora());
			caixaDetalhe.setValorDetalhe(pagamento.getValorParcela());

			caixaDetalhes.add(caixaDetalhe);

			if (pagamento.getFormasPagamento().isFlagPermitePagamentoPrazo()) {

				ContasPagar contasPagar = new ContasPagar();
				contasPagar.setBaixado(false);
				contasPagar.setCaixaCabecalho(objetoPersistir.getCaixaCabecalho());
				contasPagar.setDiarioDetalhe(diarioCredito);
				contasPagar.setCliente(objetoPersistir.getCliente());
				contasPagar.setCodigoStatus(TipoStatusEnum.ATIVO.getCodigo());
				contasPagar.setDataCadastro(new Date());
				contasPagar.setDataManutencao(new Date());
				contasPagar.setDataMovimento(pagamento.getDataLancamento());
				contasPagar.setDataVencimento(pagamento.getDataVencimento());
				contasPagar.setFormasPagamento(pagamento.getFormasPagamento());
				contasPagar.setHistorico(objetoPersistir.getHistorico());
				contasPagar.setValorAcrescimo(BigDecimal.ZERO);
				contasPagar.setValorDesconto(BigDecimal.ZERO);
				contasPagar.setValorJuros(BigDecimal.ZERO);
				contasPagar.setValorPago(BigDecimal.ZERO);
				contasPagar.setValorParcela(pagamento.getValorParcela());
				contasPagar.setContasPagarVeiculos(new ArrayList<>());

				contasPagars.add(contasPagar);
			}
		});

		if (!contasPagars.isEmpty()) {

			objetoPersistir.getFaturamentoEntradasDetalhes()
					.forEach(detalhe -> criarVinculosContasPagarVeiculo(contasPagars, detalhe, detalhe.getVeiculo()));
		}

		entityManager.persist(objetoPersistir);

		entityManager.persist(diarioCabecalho);

		if (!ListUtil.isNullOrEmpty(contasPagars)) {
			contasPagars.forEach(conta -> {

				entityManager.persist(conta);

				if (!ListUtil.isNullOrEmpty(conta.getContasPagarVeiculos())) {

					conta.getContasPagarVeiculos().forEach(entityManager::persist);
				}
			});

		}

		if (!ListUtil.isNullOrEmpty(alocacaoCustos)) {

			alocacaoCustos.forEach(entityManager::persist);
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

		if (!ListUtil.isNullOrEmpty(contasPagars)) {

			contasPagars.stream().map(detalhe -> {
				VinculoEntradaContasPagar vinculoEntradaContasPagar = new VinculoEntradaContasPagar();
				vinculoEntradaContasPagar.setContasPagar(detalhe);
				vinculoEntradaContasPagar.setFaturamentoEntrada(objetoPersistir);
				return vinculoEntradaContasPagar;
			}).forEach(entityManager::persist);
		}
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

		if (!historicoCustoDAO.existeHistorico(objetoPersistir.getHistorico())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_HISTORICO_CUSTO_NAO_CONFIGURADO, objetoPersistir.getHistorico().getDescricao());
		}

		BigDecimal valorPagamentos = objetoPersistir.getFaturamentoEntradaPagamentos().stream().map(FaturamentoEntradaPagamentos::getValorParcela)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		if (valorPagamentos.compareTo(objetoPersistir.getValorBruto()) != 0) {

			DecimalFormat decimalFormat = new DecimalFormat("0.00");

			throw new SysDescException(MensagemConstants.MENSAGEM_DIVERGENCIA_VALORES_PAGAMENTO,
					decimalFormat.format(objetoPersistir.getValorBruto().doubleValue()), decimalFormat.format(valorPagamentos.doubleValue()),
					decimalFormat.format(objetoPersistir.getValorBruto().subtract(valorPagamentos).doubleValue()));
		}

	}

	public List<FaturamentoProjection> filtrarFaturamento(PesquisaFaturamentoVO pesquisaFaturamentoVO) {

		List<FaturamentoProjection> projections = faturamentoEntradaDAO.filtrarFaturamento(pesquisaFaturamentoVO);

		if (pesquisaFaturamentoVO.getCodigoVeiculo() != null && projections.stream().anyMatch(entrada -> entrada.getVeiculo().contains("TODOS"))) {

			List<FaturamentoVeiculoProjection> faturamentoBrutoSaidaReportProjections = faturamentoCabecalhoDAO
					.buscarFaturamentoVeiculo(pesquisaFaturamentoVO.getDataMovimentoInicial(), pesquisaFaturamentoVO.getDataMovimentoFinal());

			BigDecimal valorTotal = faturamentoBrutoSaidaReportProjections.stream().map(FaturamentoVeiculoProjection::getValorBruto)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			Optional<FaturamentoVeiculoProjection> optional = faturamentoBrutoSaidaReportProjections.stream()
					.filter(item -> item.getCodigoVeiculo().equals(pesquisaFaturamentoVO.getCodigoVeiculo())).findFirst();

			projections.stream().filter(entrada -> entrada.getVeiculo().contains("TODOS")).forEach(entrada -> {

				if (!optional.isPresent()) {

					entrada.setValorBruto(BigDecimal.ZERO);
				} else {

					entrada.setValorBruto(entrada.getValorBruto().divide(valorTotal, 8, RoundingMode.HALF_EVEN)
							.multiply(optional.get().getValorBruto()).setScale(2, RoundingMode.HALF_EVEN));
				}
			});

		}

		return projections;
	}

	public void criarVinculosContasPagarVeiculo(List<ContasPagar> contasPagar, FaturamentoEntradasDetalhe detalhe, Veiculo novoVeiculo) {

		FaturamentoEntradasCabecalho cabecalho = detalhe.getFaturamentoEntradasCabecalho();
		BigDecimal valorDetalhe = detalhe.getValorBruto().subtract(detalhe.getValorDesconto()).add(detalhe.getValorAcrescimo());

		List<ContasPagarVeiculo> contasPagarVeiculos = contasPagar.stream()
				.map(contaPagar -> criarContasPagarVeiculo(detalhe, cabecalho, valorDetalhe, contaPagar)).collect(Collectors.toList());

		RateioUtil.efetuarRateio(contasPagarVeiculos, ContasPagarVeiculo::getValorParcela, ContasPagarVeiculo::setValorParcela, valorDetalhe);
	}

	private ContasPagarVeiculo criarContasPagarVeiculo(FaturamentoEntradasDetalhe detalhe, FaturamentoEntradasCabecalho cabecalho,
			BigDecimal valorDetalhe, ContasPagar contaPagar) {

		ContasPagarVeiculo contasPagarVeiculo = new ContasPagarVeiculo();
		contasPagarVeiculo.setContasPagar(contaPagar);
		contasPagarVeiculo.setDocumento(detalhe.getNumeroDocumento());
		contasPagarVeiculo.setMotorista(detalhe.getMotorista());
		contasPagarVeiculo.setVeiculo(detalhe.getVeiculo());
		contasPagarVeiculo
				.setValorParcela(contaPagar.getValorParcela().multiply(valorDetalhe).divide(cabecalho.getValorBruto(), 2, RoundingMode.HALF_EVEN));

		VinculoEntradaContasPagarVeiculo vinculoEntradaContasPagarVeiculo = new VinculoEntradaContasPagarVeiculo();
		vinculoEntradaContasPagarVeiculo.setContasPagarVeiculo(contasPagarVeiculo);
		vinculoEntradaContasPagarVeiculo.setFaturamentoEntradasDetalhe(detalhe);

		detalhe.getVinculoEntradaContasPagarVeiculos().add(vinculoEntradaContasPagarVeiculo);
		contaPagar.getContasPagarVeiculos().add(contasPagarVeiculo);

		return contasPagarVeiculo;
	}

}
