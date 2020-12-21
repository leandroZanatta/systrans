package br.com.lar.service.contaspagar;

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
import br.com.lar.repository.model.PlanoContas;
import br.com.lar.repository.projection.PagamentoContasProjection;
import br.com.lar.service.caixa.CaixaService;
import br.com.lar.service.operacao.OperacaoFinanceiroService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.constants.MensagemUtilConstants;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum;
import br.com.systrans.util.vo.PesquisaContasVO;

public class ContasPagarService extends AbstractPesquisableServiceImpl<ContasPagar> {

	private OperacaoFinanceiroService operacaoFinanceiroService = new OperacaoFinanceiroService();
	private CaixaService caixaService = new CaixaService();
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

			ContasPagar contaPagarModelo = contaPagar.getContasPagar();
			contaPagarModelo.setValorPago(contaPagarModelo.getValorPago().add(valorLiquido));
			contaPagarModelo.setBaixado(valorLiquido.compareTo(contaPagar.getValorParcela()) == 0);

			CaixaDetalhe caixaDetalheCredor = new CaixaDetalhe();
			caixaDetalheCredor.setCaixaCabecalho(caixaCabecalho);
			caixaDetalheCredor.setDataMovimento(dataMovimento);
			caixaDetalheCredor.setTipoSaldo(TipoHistoricoOperacaoEnum.CREDOR.getCodigo());
			caixaDetalheCredor.setPlanoContas(operacaoFinanceiro.getContaCredora());
			caixaDetalheCredor.setValorDetalhe(valorLiquido);

			CaixaDetalhe caixaDetalheDevedor = new CaixaDetalhe();
			caixaDetalheDevedor.setCaixaCabecalho(caixaCabecalho);
			caixaDetalheDevedor.setDataMovimento(dataMovimento);
			caixaDetalheDevedor.setTipoSaldo(TipoHistoricoOperacaoEnum.DEVEDOR.getCodigo());
			caixaDetalheDevedor.setPlanoContas(operacaoFinanceiro.getContaDevedora());
			caixaDetalheDevedor.setValorDetalhe(valorLiquido);

			DiarioCabecalho diarioCabecalho = new DiarioCabecalho();
			diarioCabecalho.setCaixaCabecalho(caixaCabecalho);
			diarioCabecalho.setDataMovimento(dataMovimento);
			diarioCabecalho.setHistorico(operacaoFinanceiro.getHistorico());

			DiarioDetalhe diarioCredito = new DiarioDetalhe();
			diarioCredito.setDiarioCabecalho(diarioCabecalho);
			diarioCredito.setPlanoContas(operacaoFinanceiro.getContaDevedora());
			diarioCredito.setTipoSaldo(TipoHistoricoOperacaoEnum.CREDOR.getCodigo());
			diarioCredito.setValorDetalhe(valorLiquido);

			DiarioDetalhe diarioDebito = new DiarioDetalhe();
			diarioDebito.setDiarioCabecalho(diarioCabecalho);
			diarioDebito.setPlanoContas(operacaoFinanceiro.getContaCredora());
			diarioDebito.setTipoSaldo(TipoHistoricoOperacaoEnum.DEVEDOR.getCodigo());
			diarioDebito.setValorDetalhe(valorLiquido);

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

	private BigDecimal calcularValorLiquido(PagamentoContasProjection contaPagar) {

		return contaPagar.getValorPagar().add(contaPagar.getDecontos()).subtract(contaPagar.getJuros()).subtract(contaPagar.getAcrescimos());
	}

}
