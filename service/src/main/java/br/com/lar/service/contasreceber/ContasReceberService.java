package br.com.lar.service.contasreceber;

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

import br.com.lar.repository.dao.ContasReceberDAO;
import br.com.lar.repository.model.CaixaCabecalho;
import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.model.ContasReceberPagamento;
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

public class ContasReceberService extends AbstractPesquisableServiceImpl<ContasReceber> {

	private ContasReceberDAO contasReceberDAO;
	private CaixaService caixaService = new CaixaService();
	private OperacaoFinanceiroService operacaoFinanceiroService = new OperacaoFinanceiroService();
	private ParametroOperacaoFinanceiraService parametroOperacaoFinanceiraService = new ParametroOperacaoFinanceiraService();

	public ContasReceberService() {
		this(new ContasReceberDAO());
	}

	public ContasReceberService(ContasReceberDAO contasReceberDAO) {
		super(contasReceberDAO, ContasReceber::getIdContasReceber);

		this.contasReceberDAO = contasReceberDAO;
	}

	public List<ContasReceber> filtrarContasReceber(PesquisaContasVO pesquisaContasReceberVO) {

		return contasReceberDAO.filtrarContasReceber(pesquisaContasReceberVO);
	}

	public void baixarContas(List<PagamentoContasProjection<ContasReceber>> contasRecebers, FormasPagamento formasPagamento,
			CaixaCabecalho caixaCabecalho) {

		caixaService.verificarCaixaAberto(caixaCabecalho);

		List<ContasReceberPagamento> contasReceberPagamentos = new ArrayList<>();
		List<DiarioCabecalho> diarioCabecalhos = new ArrayList<>();
		List<CaixaDetalhe> caixaDetalhes = new ArrayList<>();
		List<ContasReceber> contasReceberModelo = new ArrayList<>();

		contasRecebers.forEach(contaReceber -> {

			DiarioDetalhe diarioDetalhes = contaReceber.getConta().getDiarioDetalhe();

			PlanoContas planoContas = diarioDetalhes.getPlanoContas();

			OperacaoFinanceiro operacaoFinanceiro = operacaoFinanceiroService.buscarPorPlanoCredorEFormaPagamento(
					planoContas.getIdPlanoContas(),
					formasPagamento.getIdFormaPagamento());

			if (operacaoFinanceiro == null) {

				String conta = String.format("%s - %s", planoContas.getIdentificador(), planoContas.getDescricao());

				throw new SysDescException(MensagemConstants.MENSAGEM_OPERACAO_FINANCEIRO_NAO_ENCONTRADA, conta, formasPagamento.getDescricao());
			}

			Date dataMovimento = new Date();

			ContasReceberPagamento contasReceberPagamento = new ContasReceberPagamento();
			contasReceberPagamento.setCaixaCabecalho(caixaCabecalho);
			contasReceberPagamento.setContasReceber(contaReceber.getConta());
			contasReceberPagamento.setDataManutencao(dataMovimento);
			contasReceberPagamento.setDataCadastro(dataMovimento);
			contasReceberPagamento.setDataMovimento(caixaCabecalho.getDataMovimento());
			contasReceberPagamento.setFormasPagamento(formasPagamento);
			contasReceberPagamento.setHistorico(operacaoFinanceiro.getHistorico());
			contasReceberPagamento.setValorAcrescimo(contaReceber.getAcrescimos());
			contasReceberPagamento.setValorDesconto(contaReceber.getDecontos());
			contasReceberPagamento.setValorJuros(contaReceber.getJuros());
			contasReceberPagamento.setValorPago(contaReceber.getValorPagar());

			BigDecimal valorLiquido = calcularValorLiquido(contaReceber);

			CaixaDetalhe caixaDetalheCredor = new CaixaDetalhe();
			caixaDetalheCredor.setCaixaCabecalho(caixaCabecalho);
			caixaDetalheCredor.setDataMovimento(dataMovimento);
			caixaDetalheCredor.setTipoSaldo(CREDOR.getCodigo());
			caixaDetalheCredor.setPlanoContas(operacaoFinanceiro.getContaDevedora());
			caixaDetalheCredor.setValorDetalhe(valorLiquido);

			CaixaDetalhe caixaDetalheDevedor = new CaixaDetalhe();
			caixaDetalheDevedor.setCaixaCabecalho(caixaCabecalho);
			caixaDetalheDevedor.setDataMovimento(dataMovimento);
			caixaDetalheDevedor.setTipoSaldo(DEVEDOR.getCodigo());
			caixaDetalheDevedor.setPlanoContas(operacaoFinanceiro.getContaCredora());
			caixaDetalheDevedor.setValorDetalhe(valorLiquido);

			DiarioCabecalho diarioCabecalho = new DiarioCabecalho();
			diarioCabecalho.setCaixaCabecalho(caixaCabecalho);
			diarioCabecalho.setDataMovimento(dataMovimento);
			diarioCabecalho.setHistorico(operacaoFinanceiro.getHistorico());

			DiarioDetalhe diarioCredito = new DiarioDetalhe();
			diarioCredito.setDiarioCabecalho(diarioCabecalho);
			diarioCredito.setPlanoContas(operacaoFinanceiro.getContaCredora());
			diarioCredito.setTipoSaldo(CREDOR.getCodigo());
			diarioCredito.setValorDetalhe(valorLiquido);

			DiarioDetalhe diarioDebito = new DiarioDetalhe();
			diarioDebito.setDiarioCabecalho(diarioCabecalho);
			diarioDebito.setPlanoContas(operacaoFinanceiro.getContaDevedora());
			diarioDebito.setTipoSaldo(DEVEDOR.getCodigo());
			diarioDebito.setValorDetalhe(valorLiquido);

			gerarDescontosAcrescimos(caixaCabecalho, formasPagamento, contaReceber, dataMovimento, diarioCabecalhos, caixaDetalhes);

			ContasReceber contasReceber = contaReceber.getConta();
			contasReceber.setValorPago(contasReceber.getValorPago().add(contaReceber.getValorPagar()));
			contasReceber.setBaixado(valorLiquido.compareTo(contaReceber.getValorParcela()) == 0);

			diarioCabecalho.setDiarioDetalhes(Arrays.asList(diarioCredito, diarioDebito));
			contasReceberPagamentos.add(contasReceberPagamento);
			diarioCabecalhos.add(diarioCabecalho);
			caixaDetalhes.add(caixaDetalheCredor);
			caixaDetalhes.add(caixaDetalheDevedor);
			contasReceberModelo.add(contasReceber);

		});

		EntityManager em = contasReceberDAO.getEntityManager();

		try {
			em.getTransaction().begin();

			contasReceberPagamentos.forEach(em::persist);

			contasReceberModelo.forEach(em::persist);

			diarioCabecalhos.forEach(em::persist);

			caixaDetalhes.forEach(em::persist);

			em.getTransaction().commit();
		} catch (Exception e) {

			em.getTransaction().rollback();

			throw new SysDescException(MensagemUtilConstants.MENSAGEM_TIPO_DADO_INVALIDO);
		}
	}

	private void gerarDescontosAcrescimos(CaixaCabecalho caixaCabecalho, FormasPagamento pagamento,
			PagamentoContasProjection<ContasReceber> contasReceber,
			Date dataMovimento, List<DiarioCabecalho> diarios, List<CaixaDetalhe> caixaDetalhes) {

		ContasReceber conta = contasReceber.getConta();

		if (BigDecimalUtil.maior(contasReceber.getDecontos(), BigDecimal.ZERO)) {

			gerarParametrosFinanceiros(contasReceber.getDecontos(), DESCONTOS, pagamento, caixaCabecalho, dataMovimento, diarios, caixaDetalhes);

			conta.setValorDesconto(contasReceber.getDecontos());
		}

		if (BigDecimalUtil.maior(contasReceber.getAcrescimos(), BigDecimal.ZERO)) {

			gerarParametrosFinanceiros(contasReceber.getAcrescimos(), ACRESCIMOS, pagamento, caixaCabecalho, dataMovimento, diarios, caixaDetalhes);

			conta.setValorAcrescimo(contasReceber.getAcrescimos());
		}

		if (BigDecimalUtil.maior(contasReceber.getJuros(), BigDecimal.ZERO)) {

			gerarParametrosFinanceiros(contasReceber.getJuros(), JUROS, pagamento, caixaCabecalho, dataMovimento, diarios, caixaDetalhes);

			conta.setValorJuros(contasReceber.getJuros());
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

	private BigDecimal calcularValorLiquido(PagamentoContasProjection<ContasReceber> contaReceber) {

		return contaReceber.getValorPagar().add(contaReceber.getDecontos()).subtract(contaReceber.getJuros()).subtract(contaReceber.getAcrescimos());
	}

}
