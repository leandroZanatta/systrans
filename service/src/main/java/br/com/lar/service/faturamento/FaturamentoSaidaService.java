package br.com.lar.service.faturamento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import br.com.lar.repository.dao.FaturamentoCabecalhoDAO;
import br.com.lar.repository.dao.OperacaoDAO;
import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.model.ContasReceberVeiculo;
import br.com.lar.repository.model.DiarioCabecalho;
import br.com.lar.repository.model.DiarioDetalhe;
import br.com.lar.repository.model.FaturamentoCabecalho;
import br.com.lar.repository.model.FaturamentoPagamentos;
import br.com.lar.repository.model.Operacao;
import br.com.lar.repository.model.VinculoSaida;
import br.com.lar.repository.model.VinculoSaidaCaixa;
import br.com.lar.repository.model.VinculoSaidaContasReceber;
import br.com.lar.service.caixa.CaixaService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.enumeradores.TipoStatusEnum;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.RateioUtil;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum;

public class FaturamentoSaidaService extends AbstractPesquisableServiceImpl<FaturamentoCabecalho> {

	private FaturamentoCabecalhoDAO faturamentoDAO;
	private CaixaService caixaService = new CaixaService();
	private OperacaoDAO operacaoDAO = new OperacaoDAO();

	public FaturamentoSaidaService() {
		this(new FaturamentoCabecalhoDAO());
	}

	public FaturamentoSaidaService(FaturamentoCabecalhoDAO faturamentoDAO) {
		super(faturamentoDAO, FaturamentoCabecalho::getIdFaturamentoCabecalho);

		this.faturamentoDAO = faturamentoDAO;
	}

	@Override
	public void validar(FaturamentoCabecalho objetoPersistir) {

		if (objetoPersistir.getHistorico() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_HISTORICO);
		}

		if (objetoPersistir.getCaixaCabecalho() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_CAIXA_NAO_ENCONTRADO);
		}

		if (objetoPersistir.getCliente() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_FORNECEDOR);
		}

		if (ListUtil.isNullOrEmpty(objetoPersistir.getFaturamentoPagamentos())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_PAGAMENTOS);
		}

		if (objetoPersistir.getDataMovimento() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_DATA_MOVIMENTO_INVALIDA);
		}

		BigDecimal valorPagamentos = objetoPersistir.getFaturamentoPagamentos().stream().map(FaturamentoPagamentos::getValorParcela)
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
	public void salvar(FaturamentoCabecalho objetoPersistir) {

		caixaService.verificarCaixaAberto(objetoPersistir.getCaixaCabecalho());

		DiarioCabecalho diarioCabecalho = new DiarioCabecalho();
		diarioCabecalho.setDataMovimento(new Date());
		diarioCabecalho.setHistorico(objetoPersistir.getHistorico());
		diarioCabecalho.setDiarioDetalhes(new ArrayList<>());
		diarioCabecalho.setCaixaCabecalho(objetoPersistir.getCaixaCabecalho());

		List<ContasReceber> contasRecebers = new ArrayList<>();
		List<CaixaDetalhe> caixaDetalhes = new ArrayList<>();

		objetoPersistir.getFaturamentoPagamentos().forEach(recebimento -> {

			Operacao operacao = operacaoDAO.buscarOperacao(objetoPersistir.getHistorico().getIdHistorico(),
					recebimento.getFormasPagamento().getIdFormaPagamento());

			DiarioDetalhe diarioCredito = new DiarioDetalhe();
			diarioCredito.setDiarioCabecalho(diarioCabecalho);
			diarioCredito.setPlanoContas(operacao.getContaCredora());
			diarioCredito.setTipoSaldo(TipoHistoricoOperacaoEnum.CREDOR.getCodigo());
			diarioCredito.setValorDetalhe(recebimento.getValorParcela());

			DiarioDetalhe diarioDebito = new DiarioDetalhe();
			diarioDebito.setDiarioCabecalho(diarioCabecalho);
			diarioDebito.setPlanoContas(operacao.getContaDevedora());
			diarioDebito.setTipoSaldo(TipoHistoricoOperacaoEnum.DEVEDOR.getCodigo());
			diarioDebito.setValorDetalhe(recebimento.getValorParcela());

			diarioCabecalho.getDiarioDetalhes().add(diarioDebito);
			diarioCabecalho.getDiarioDetalhes().add(diarioCredito);

			CaixaDetalhe caixaDetalhe = new CaixaDetalhe();
			caixaDetalhe.setCaixaCabecalho(objetoPersistir.getCaixaCabecalho());
			caixaDetalhe.setDataMovimento(new Date());
			caixaDetalhe.setTipoSaldo(operacao.getHistorico().getTipoHistorico());
			caixaDetalhe.setPlanoContas(operacao.getContaCredora());
			caixaDetalhe.setValorDetalhe(recebimento.getValorParcela());

			caixaDetalhes.add(caixaDetalhe);

			if (recebimento.getFormasPagamento().isFlagPermitePagamentoPrazo()) {

				ContasReceber contasReceber = new ContasReceber();
				contasReceber.setBaixado(false);
				contasReceber.setCaixaCabecalho(objetoPersistir.getCaixaCabecalho());
				contasReceber.setDiarioDetalhe(diarioDebito);
				contasReceber.setCliente(objetoPersistir.getCliente());
				contasReceber.setCodigoStatus(TipoStatusEnum.ATIVO.getCodigo());
				contasReceber.setDataCadastro(new Date());
				contasReceber.setDataManutencao(new Date());
				contasReceber.setDataMovimento(recebimento.getDataLancamento());
				contasReceber.setDataVencimento(recebimento.getDataVencimento());
				contasReceber.setFormasPagamento(recebimento.getFormasPagamento());
				contasReceber.setHistorico(objetoPersistir.getHistorico());
				contasReceber.setValorAcrescimo(BigDecimal.ZERO);
				contasReceber.setValorDesconto(BigDecimal.ZERO);
				contasReceber.setValorJuros(BigDecimal.ZERO);
				contasReceber.setValorPago(BigDecimal.ZERO);
				contasReceber.setValorParcela(recebimento.getValorParcela());

				List<ContasReceberVeiculo> contasReceberVeiculos = objetoPersistir.getFaturamentoDetalhes().stream().map(detalhe -> {

					BigDecimal valorDetalhe = detalhe.getValorBruto().subtract(detalhe.getValorDesconto()).add(detalhe.getValorAcrescimo());

					ContasReceberVeiculo contasReceberVeiculo = new ContasReceberVeiculo();
					contasReceberVeiculo.setContasReceber(contasReceber);
					contasReceberVeiculo.setDocumento(detalhe.getNumeroDocumento());
					contasReceberVeiculo.setMotorista(detalhe.getMotorista());
					contasReceberVeiculo.setVeiculo(detalhe.getVeiculo());
					contasReceberVeiculo.setValorParcela(
							valorDetalhe.multiply(recebimento.getValorParcela()).divide(objetoPersistir.getValorBruto(), 2,
									RoundingMode.HALF_EVEN));

					return contasReceberVeiculo;
				}).collect(Collectors.toList());

				RateioUtil.efetuarRateio(contasReceberVeiculos, ContasReceberVeiculo::getValorParcela, ContasReceberVeiculo::setValorParcela,
						recebimento.getValorParcela());

				contasReceber.setContasReceberVeiculos(contasReceberVeiculos);

				contasRecebers.add(contasReceber);
			}
		});

		EntityManager entityManager = faturamentoDAO.getEntityManager();

		try {

			entityManager.getTransaction().begin();

			entityManager.persist(objetoPersistir);

			entityManager.persist(diarioCabecalho);

			if (!ListUtil.isNullOrEmpty(contasRecebers)) {

				contasRecebers.forEach(entityManager::persist);
			}

			caixaDetalhes.forEach(entityManager::persist);

			VinculoSaida vinculoSaida = new VinculoSaida();
			vinculoSaida.setDiarioCabecalho(diarioCabecalho);
			vinculoSaida.setFaturamento(objetoPersistir);
			entityManager.persist(vinculoSaida);

			caixaDetalhes.stream().map(detalhe -> {
				VinculoSaidaCaixa vinculoSaidaCaixa = new VinculoSaidaCaixa();
				vinculoSaidaCaixa.setCaixaDetalhe(detalhe);
				vinculoSaidaCaixa.setFaturamento(objetoPersistir);

				return vinculoSaidaCaixa;
			}).forEach(entityManager::persist);

			if (!ListUtil.isNullOrEmpty(contasRecebers)) {

				contasRecebers.stream().map(detalhe -> {
					VinculoSaidaContasReceber vinculoSaidaContasReceber = new VinculoSaidaContasReceber();
					vinculoSaidaContasReceber.setContasReceber(detalhe);
					vinculoSaidaContasReceber.setFaturamento(objetoPersistir);
					return vinculoSaidaContasReceber;
				}).forEach(entityManager::persist);
			}
		} finally {
			entityManager.getTransaction().commit();
		}

	}
}
