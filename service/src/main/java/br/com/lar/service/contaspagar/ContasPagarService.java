package br.com.lar.service.contaspagar;

import static br.com.systrans.util.enumeradores.TipoContaEnum.ACRESCIMOS;
import static br.com.systrans.util.enumeradores.TipoContaEnum.DESCONTOS;
import static br.com.systrans.util.enumeradores.TipoContaEnum.JUROS;
import static br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum.CREDOR;
import static br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum.DEVEDOR;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import br.com.lar.repository.dao.ContasPagarDAO;
import br.com.lar.repository.model.CaixaCabecalho;
import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.ContasPagarPagamento;
import br.com.lar.repository.model.DiarioCabecalho;
import br.com.lar.repository.model.DiarioDetalhe;
import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.model.OperacaoFinanceiro;
import br.com.lar.repository.model.ParametroOperacaoFinanceira;
import br.com.lar.repository.model.PlanoContas;
import br.com.lar.repository.projection.PagamentoContasProjection;
import br.com.lar.service.caixa.CaixaService;
import br.com.lar.service.operacao.OperacaoFinanceiroService;
import br.com.lar.service.operacao.ParametroOperacaoFinanceiraService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.constants.MensagemUtilConstants;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.enumeradores.TipoContaEnum;
import br.com.systrans.util.vo.PesquisaContasVO;

public class ContasPagarService extends AbstractPesquisableServiceImpl<ContasPagar> {

	private OperacaoFinanceiroService operacaoFinanceiroService = new OperacaoFinanceiroService();
	private CaixaService caixaService = new CaixaService();
	private ParametroOperacaoFinanceiraService parametroOperacaoFinanceiraService = new ParametroOperacaoFinanceiraService();
	private ContasPagarDAO contasPagarDAO;

	public ContasPagarService() {
		this(new ContasPagarDAO());
	}

	public ContasPagarService(ContasPagarDAO contasPagarDAO) {
		super(contasPagarDAO, ContasPagar::getIdContasPagar);

		this.contasPagarDAO = contasPagarDAO;
	}

	public List<ContasPagar> filtrarContasPagar(PesquisaContasVO pesquisaContasReceberVO) {

		return contasPagarDAO.filtrarContasPagar(pesquisaContasReceberVO);
	}

	public void baixarContas(List<PagamentoContasProjection> contasPagars, FormasPagamento formasPagamento, CaixaCabecalho caixaCabecalho) {

		caixaService.verificarCaixaAberto(caixaCabecalho);

		List<ContasPagarPagamento> contasPagarPagamentos = new ArrayList<>();
		List<DiarioCabecalho> diarioCabecalhos = new ArrayList<>();
		List<CaixaDetalhe> caixaDetalhes = new ArrayList<>();
		List<ContasPagar> contasPagarsModelo = new ArrayList<>();

		contasPagars.forEach(contaPagar -> {

			DiarioDetalhe diarioDetalhes = contaPagar.getContasPagar().getDiarioDetalhe();

			PlanoContas planoContas = diarioDetalhes.getPlanoContas();

			OperacaoFinanceiro operacaoFinanceiro = operacaoFinanceiroService.buscarPorPlanoCredorEFormaPagamento(
					planoContas.getIdPlanoContas(),
					formasPagamento.getIdFormaPagamento());

			if (operacaoFinanceiro == null) {

				String conta = String.format("%s - %s", planoContas.getIdentificador(), planoContas.getDescricao());

				throw new SysDescException(MensagemConstants.MENSAGEM_OPERACAO_FINANCEIRO_NAO_ENCONTRADA, conta, formasPagamento.getDescricao());
			}

			Date dataMovimento = new Date();

			ContasPagarPagamento contasPagarPagamento = new ContasPagarPagamento();
			contasPagarPagamento.setCaixaCabecalho(caixaCabecalho);
			contasPagarPagamento.setContasPagar(contaPagar.getContasPagar());
			contasPagarPagamento.setDataManutencao(dataMovimento);
			contasPagarPagamento.setDataCadastro(dataMovimento);
			contasPagarPagamento.setDataMovimento(caixaCabecalho.getDataMovimento());
			contasPagarPagamento.setFormasPagamento(formasPagamento);
			contasPagarPagamento.setHistorico(operacaoFinanceiro.getHistorico());
			contasPagarPagamento.setValorAcrescimo(contaPagar.getAcrescimos());
			contasPagarPagamento.setValorDesconto(contaPagar.getDecontos());
			contasPagarPagamento.setValorJuros(contaPagar.getJuros());
			contasPagarPagamento.setValorPago(contaPagar.getValorPagar());

			BigDecimal valorLiquido = calcularValorLiquido(contaPagar);

			CaixaDetalhe caixaDetalheCredor = new CaixaDetalhe();
			caixaDetalheCredor.setCaixaCabecalho(caixaCabecalho);
			caixaDetalheCredor.setDataMovimento(dataMovimento);
			caixaDetalheCredor.setTipoSaldo(CREDOR.getCodigo());
			caixaDetalheCredor.setPlanoContas(operacaoFinanceiro.getContaCredora());
			caixaDetalheCredor.setValorDetalhe(valorLiquido);

			CaixaDetalhe caixaDetalheDevedor = new CaixaDetalhe();
			caixaDetalheDevedor.setCaixaCabecalho(caixaCabecalho);
			caixaDetalheDevedor.setDataMovimento(dataMovimento);
			caixaDetalheDevedor.setTipoSaldo(DEVEDOR.getCodigo());
			caixaDetalheDevedor.setPlanoContas(operacaoFinanceiro.getContaDevedora());
			caixaDetalheDevedor.setValorDetalhe(valorLiquido);

			DiarioCabecalho diarioCabecalho = new DiarioCabecalho();
			diarioCabecalho.setCaixaCabecalho(caixaCabecalho);
			diarioCabecalho.setDataMovimento(dataMovimento);
			diarioCabecalho.setHistorico(operacaoFinanceiro.getHistorico());

			DiarioDetalhe diarioCredito = new DiarioDetalhe();
			diarioCredito.setDiarioCabecalho(diarioCabecalho);
			diarioCredito.setPlanoContas(operacaoFinanceiro.getContaDevedora());
			diarioCredito.setTipoSaldo(CREDOR.getCodigo());
			diarioCredito.setValorDetalhe(valorLiquido);

			DiarioDetalhe diarioDebito = new DiarioDetalhe();
			diarioDebito.setDiarioCabecalho(diarioCabecalho);
			diarioDebito.setPlanoContas(operacaoFinanceiro.getContaCredora());
			diarioDebito.setTipoSaldo(DEVEDOR.getCodigo());
			diarioDebito.setValorDetalhe(valorLiquido);

			gerarDescontosAcrescimos(caixaCabecalho, formasPagamento, contaPagar, dataMovimento, diarioCabecalhos, caixaDetalhes);

			ContasPagar contaPagarModelo = contaPagar.getContasPagar();
			contaPagarModelo.setValorPago(contaPagarModelo.getValorPago().add(contaPagar.getValorPagar()));
			contaPagarModelo.setBaixado(valorLiquido.compareTo(contaPagar.getValorParcela()) == 0);

			diarioCabecalho.setDiarioDetalhes(Arrays.asList(diarioCredito, diarioDebito));
			contasPagarPagamentos.add(contasPagarPagamento);
			diarioCabecalhos.add(diarioCabecalho);
			caixaDetalhes.add(caixaDetalheCredor);
			caixaDetalhes.add(caixaDetalheDevedor);
			contasPagarsModelo.add(contaPagarModelo);

		});

		EntityManager em = contasPagarDAO.getEntityManager();

		try {
			em.getTransaction().begin();

			contasPagarPagamentos.forEach(em::persist);

			contasPagarsModelo.forEach(em::persist);

			diarioCabecalhos.forEach(em::persist);

			caixaDetalhes.forEach(em::persist);

			em.getTransaction().commit();
		} catch (Exception e) {

			em.getTransaction().rollback();

			throw new SysDescException(MensagemUtilConstants.MENSAGEM_TIPO_DADO_INVALIDO);
		}

	}

	private void gerarDescontosAcrescimos(CaixaCabecalho caixaCabecalho, FormasPagamento pagamento, PagamentoContasProjection contaPagar,
			Date dataMovimento, List<DiarioCabecalho> diarios, List<CaixaDetalhe> caixaDetalhes) {

		ContasPagar contasPagar = contaPagar.getContasPagar();

		if (BigDecimalUtil.maior(contaPagar.getDecontos(), BigDecimal.ZERO)) {

			gerarParametrosFinanceiros(contaPagar.getDecontos(), DESCONTOS, pagamento, caixaCabecalho, dataMovimento, diarios, caixaDetalhes);

			contasPagar.setValorDesconto(contaPagar.getDecontos());
		}

		if (BigDecimalUtil.maior(contaPagar.getAcrescimos(), BigDecimal.ZERO)) {

			gerarParametrosFinanceiros(contaPagar.getAcrescimos(), ACRESCIMOS, pagamento, caixaCabecalho, dataMovimento, diarios, caixaDetalhes);

			contasPagar.setValorAcrescimo(contaPagar.getAcrescimos());
		}

		if (BigDecimalUtil.maior(contaPagar.getJuros(), BigDecimal.ZERO)) {

			gerarParametrosFinanceiros(contaPagar.getJuros(), JUROS, pagamento, caixaCabecalho, dataMovimento, diarios, caixaDetalhes);

			contasPagar.setValorJuros(contaPagar.getJuros());
		}
	}

	private void gerarParametrosFinanceiros(BigDecimal valorVerificar, TipoContaEnum tipoContaEnum, FormasPagamento formasPagamento,
			CaixaCabecalho caixaCabecalho, Date dataMovimento, List<DiarioCabecalho> diarioCabecalhos, List<CaixaDetalhe> caixaDetalhes) {

		ParametroOperacaoFinanceira parametroFinanceiro = parametroOperacaoFinanceiraService.buscarParametroOperacao(
				DEVEDOR.getCodigo(), tipoContaEnum.getCodigo(), formasPagamento.getIdFormaPagamento());

		if (parametroFinanceiro == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_PARAMETRO_OPERACAO_FINANCEIRA_NAO_EXISTE,
					tipoContaEnum.getDescricao().toUpperCase(), formasPagamento.getDescricao());
		}

		DiarioCabecalho diarioCabecalho = new DiarioCabecalho();
		diarioCabecalho.setCaixaCabecalho(caixaCabecalho);
		diarioCabecalho.setDataMovimento(dataMovimento);
		diarioCabecalho.setHistorico(parametroFinanceiro.getHistorico());

		DiarioDetalhe diarioCredor = new DiarioDetalhe();
		diarioCredor.setDiarioCabecalho(diarioCabecalho);
		diarioCredor.setTipoSaldo(CREDOR.getCodigo());
		diarioCredor.setPlanoContas(parametroFinanceiro.getContaCredora());
		diarioCredor.setValorDetalhe(valorVerificar);

		DiarioDetalhe diarioDevedor = new DiarioDetalhe();
		diarioDevedor.setDiarioCabecalho(diarioCabecalho);
		diarioDevedor.setTipoSaldo(DEVEDOR.getCodigo());
		diarioDevedor.setPlanoContas(parametroFinanceiro.getContaDevedora());
		diarioDevedor.setValorDetalhe(valorVerificar);

		diarioCabecalho.setDiarioDetalhes(Arrays.asList(diarioCredor, diarioDevedor));

		CaixaDetalhe caixaDetalhe = new CaixaDetalhe();
		caixaDetalhe.setCaixaCabecalho(caixaCabecalho);
		caixaDetalhe.setDataMovimento(dataMovimento);
		caixaDetalhe.setTipoSaldo(tipoContaEnum.equals(TipoContaEnum.DESCONTOS) ? CREDOR.getCodigo() : DEVEDOR.getCodigo());
		caixaDetalhe.setPlanoContas(
				tipoContaEnum.equals(TipoContaEnum.DESCONTOS) ? parametroFinanceiro.getContaDevedora() : parametroFinanceiro.getContaCredora());
		caixaDetalhe.setValorDetalhe(valorVerificar);

		caixaDetalhes.add(caixaDetalhe);
		diarioCabecalhos.add(diarioCabecalho);

	}

	private BigDecimal calcularValorLiquido(PagamentoContasProjection contaPagar) {

		return contaPagar.getValorPagar().add(contaPagar.getDecontos()).subtract(contaPagar.getJuros()).subtract(contaPagar.getAcrescimos());
	}

}
