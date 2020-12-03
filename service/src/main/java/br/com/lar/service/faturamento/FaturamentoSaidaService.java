package br.com.lar.service.faturamento;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.persistence.EntityManager;

import br.com.lar.repository.dao.FaturamentoCabecalhoDAO;
import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.model.DiarioCabecalho;
import br.com.lar.repository.model.FaturamentoCabecalho;
import br.com.lar.repository.model.FaturamentoPagamentos;
import br.com.lar.repository.model.VinculoSaida;
import br.com.lar.repository.model.VinculoSaidaCaixa;
import br.com.lar.repository.model.VinculoSaidaContasReceber;
import br.com.lar.service.caixa.CaixaService;
import br.com.lar.service.caixa.FaturamentoCaixa;
import br.com.lar.service.contasreceber.FaturamentoContasReceber;
import br.com.lar.service.diario.DiarioService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class FaturamentoSaidaService extends AbstractPesquisableServiceImpl<FaturamentoCabecalho> {

	private FaturamentoCabecalhoDAO faturamentoDAO;
	private FaturamentoContasReceber faturamentoContasReceber = new FaturamentoContasReceber();
	private DiarioService faturamentoDiario = new DiarioService();
	private FaturamentoCaixa faturamentoCaixa = new FaturamentoCaixa();
	private CaixaService caixaService = new CaixaService();

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

		DiarioCabecalho diarioCabecalho = faturamentoDiario.registrarDiarioFaturamento(objetoPersistir);

		List<ContasReceber> contasReceber = faturamentoContasReceber.registrarContasReceber(objetoPersistir,
				objetoPersistir.getFaturamentoDetalhes());

		List<CaixaDetalhe> caixaDetalhes = faturamentoCaixa.registrarCaixaFaturamento(objetoPersistir);

		EntityManager entityManager = faturamentoDAO.getEntityManager();

		try {

			entityManager.getTransaction().begin();

			entityManager.persist(objetoPersistir);

			entityManager.persist(diarioCabecalho);

			if (!ListUtil.isNullOrEmpty(contasReceber)) {

				contasReceber.forEach(entityManager::persist);
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

			if (!ListUtil.isNullOrEmpty(contasReceber)) {

				contasReceber.stream().map(detalhe -> {
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
