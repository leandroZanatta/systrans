package br.com.lar.service.caixa;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import br.com.lar.repository.dao.CaixaCabecalhoDAO;
import br.com.lar.repository.dao.CaixaDAO;
import br.com.lar.repository.dao.CaixaDetalheDAO;
import br.com.lar.repository.dao.CaixaSaldoDAO;
import br.com.lar.repository.dao.DiarioCabecalhoDAO;
import br.com.lar.repository.model.Caixa;
import br.com.lar.repository.model.CaixaCabecalho;
import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.model.PlanoContas;
import br.com.lar.repository.projection.ResumoCaixaMovimentoProjection;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.IfNull;
import br.com.sysdesc.util.enumeradores.TipoSaldoEnum;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum;
import br.com.systrans.util.vo.DetalheFechamentoVO;
import br.com.systrans.util.vo.FechamentoCaixaVO;

public class CaixaCabecalhoService extends AbstractPesquisableServiceImpl<CaixaCabecalho> {

	private CaixaCabecalhoDAO caixaCabecalhoDAO;
	private CaixaDAO caixaDAO = new CaixaDAO();
	private CaixaSaldoDAO caixaSaldoDAO = new CaixaSaldoDAO();
	private DiarioCabecalhoDAO diarioCabecalhoDAO = new DiarioCabecalhoDAO();
	private CaixaDetalheDAO caixaDetalheDAO = new CaixaDetalheDAO();

	public CaixaCabecalhoService() {
		this(new CaixaCabecalhoDAO());
	}

	public CaixaCabecalhoService(CaixaCabecalhoDAO caixaCabecalhoDAO) {
		super(caixaCabecalhoDAO, CaixaCabecalho::getIdCaixaCabecalho);

		this.caixaCabecalhoDAO = caixaCabecalhoDAO;
	}

	public CaixaCabecalho validarCaixaAbertoNoDia(Usuario usuario) {

		verificarCaixaUsuario(usuario);

		CaixaCabecalho caixaCabecalho = caixaCabecalhoDAO.obterCaixaNoDia(usuario.getIdUsuario());

		if (caixaCabecalho == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_CAIXA_NAO_ENCONTRADO);
		}

		return caixaCabecalho;
	}

	public CaixaCabecalho validarExistenciaCaixaAberto(Usuario usuario) {

		verificarCaixaUsuario(usuario);

		CaixaCabecalho caixaCabecalho = caixaCabecalhoDAO.obterUltimoCaixaAberto(usuario.getIdUsuario());

		if (caixaCabecalho == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_CAIXA_NAO_ENCONTRADO);
		}

		return caixaCabecalho;
	}

	public void consultarAterturaCaixa(Usuario usuario) {

		verificarCaixaUsuario(usuario);

		boolean caixaAberto = caixaCabecalhoDAO.buscarCaixasAbertos(usuario.getIdUsuario());

		if (caixaAberto) {

			throw new SysDescException(MensagemConstants.MENSAGEM_CAIXA_ABERTO);
		}
	}

	public CaixaCabecalho obterCaixaDoDia(Usuario usuario) {

		return caixaCabecalhoDAO.obterCaixaNoDia(usuario.getIdUsuario());
	}

	public void abrirCaixa(Usuario usuario) {

		consultarAterturaCaixa(usuario);

		CaixaCabecalho caixaCabecalho = new CaixaCabecalho();

		Date dataAbertura = new Date();

		caixaCabecalho.setCaixa(caixaDAO.buscarCaixaPorUsuario(usuario.getIdUsuario()));
		caixaCabecalho.setDataAbertura(dataAbertura);
		caixaCabecalho.setDataMovimento(dataAbertura);

		caixaCabecalhoDAO.salvar(caixaCabecalho);
	}

	public void fecharCaixa(CaixaCabecalho caixaCabecalho) {
		caixaCabecalho.setDataFechamento(new Date());

		EntityManager entityManager = caixaCabecalhoDAO.getEntityManager();

		try {
			entityManager.getTransaction().begin();
			entityManager.persist(caixaCabecalho);
		} finally {
			entityManager.getTransaction().commit();
		}
	}

	public FechamentoCaixaVO buscarResumoFechamentoCaixa(CaixaCabecalho caixaCabecalho) {

		Map<String, List<ResumoCaixaMovimentoProjection>> resumoMovimentos = diarioCabecalhoDAO
				.buscarResumoCaixa(caixaCabecalho.getIdCaixaCabecalho()).stream()
				.collect(Collectors.groupingBy(ResumoCaixaMovimentoProjection::getTipoSaldo));

		BigDecimal resumoCreditos = getValorResumo(resumoMovimentos, TipoSaldoEnum.CREDOR);
		BigDecimal resumoDebitos = getValorResumo(resumoMovimentos, TipoSaldoEnum.DEVEDOR);

		List<CaixaDetalhe> caixaDetalhes = caixaDetalheDAO.buscarCaixaDetalhes(caixaCabecalho.getIdCaixaCabecalho());

		validarConsistenciaCaixa(resumoCreditos.subtract(resumoDebitos), caixaDetalhes);

		Map<PlanoContas, List<CaixaDetalhe>> contas = caixaDetalhes.stream()
				.filter(detalhe -> !detalhe.getPlanoContas().getIdPlanoContas().equals(4L))
				.collect(Collectors.groupingBy(CaixaDetalhe::getPlanoContas));

		List<DetalheFechamentoVO> detalheFechamentoVOs = new ArrayList<>();

		contas.forEach((key, value) -> {

			DetalheFechamentoVO detalheFechamentoVO = new DetalheFechamentoVO();
			detalheFechamentoVO.setNomeConta(key.getDescricao());

			BigDecimal saldoCredito = value.stream()
					.filter(detalhe -> detalhe.getTipoSaldo().equals(TipoHistoricoOperacaoEnum.CREDOR.getCodigo()))
					.map(CaixaDetalhe::getValorDetalhe).reduce(BigDecimal.ZERO, BigDecimal::add);

			BigDecimal saldoDebito = value.stream()
					.filter(detalhe -> detalhe.getTipoSaldo().equals(TipoHistoricoOperacaoEnum.DEVEDOR.getCodigo()))
					.map(CaixaDetalhe::getValorDetalhe).reduce(BigDecimal.ZERO, BigDecimal::add);

			detalheFechamentoVO.setValorConta(saldoCredito.subtract(saldoDebito));

			detalheFechamentoVOs.add(detalheFechamentoVO);

		});

		BigDecimal valorPagamentos = detalheFechamentoVOs.stream().map(DetalheFechamentoVO::getValorConta)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Map<Long, List<CaixaDetalhe>> saldoDinheiro = caixaDetalhes.stream()
				.filter(detalhe -> detalhe.getPlanoContas().getIdPlanoContas().equals(4L))
				.collect(Collectors.groupingBy(CaixaDetalhe::getTipoSaldo));

		BigDecimal resumoCreditosDinheiro = getValorSaldo(saldoDinheiro, TipoHistoricoOperacaoEnum.CREDOR);
		BigDecimal resumoDebitosDinheiro = getValorSaldo(saldoDinheiro, TipoHistoricoOperacaoEnum.DEVEDOR);

		FechamentoCaixaVO fechamentoCaixaVO = new FechamentoCaixaVO();
		fechamentoCaixaVO.setDetalheFechamentoVOs(detalheFechamentoVOs);
		fechamentoCaixaVO.setSaldoAtual(IfNull.get(caixaSaldoDAO.buscarUltimoSaldoCaixa(caixaCabecalho.getCaixa().getIdCaixa()), BigDecimal.ZERO));
		fechamentoCaixaVO.setValorDinheiro(resumoCreditosDinheiro.subtract(resumoDebitosDinheiro));
		fechamentoCaixaVO.setValorMovimentado(resumoCreditos.subtract(resumoDebitos));
		fechamentoCaixaVO.setValorPagamentos(valorPagamentos);

		return fechamentoCaixaVO;
	}

	private void validarConsistenciaCaixa(BigDecimal resumoMovimento, List<CaixaDetalhe> caixaDetalhes) {

		Map<Long, List<CaixaDetalhe>> saldo = caixaDetalhes.stream()
				.collect(Collectors.groupingBy(CaixaDetalhe::getTipoSaldo));

		BigDecimal saldoCredor = getValorSaldo(saldo, TipoHistoricoOperacaoEnum.CREDOR);
		BigDecimal saldoDevedor = getValorSaldo(saldo, TipoHistoricoOperacaoEnum.DEVEDOR);

		BigDecimal resumoPagamentos = saldoCredor.subtract(saldoDevedor);

		if (BigDecimalUtil.diferente(resumoMovimento, resumoPagamentos)) {

			DecimalFormat decimalFormat = new DecimalFormat("0.00");

			throw new SysDescException(MensagemConstants.MENSAGEM_RESUMO_CAIXA_INVALIDO, decimalFormat.format(resumoMovimento.doubleValue()),
					decimalFormat.format(resumoPagamentos.doubleValue()),
					decimalFormat.format(resumoMovimento.subtract(resumoPagamentos).doubleValue()));
		}

	}

	private BigDecimal getValorSaldo(Map<Long, List<CaixaDetalhe>> saldo, TipoHistoricoOperacaoEnum tipoOperacao) {

		if (saldo.containsKey(tipoOperacao.getCodigo())) {

			return IfNull
					.get(saldo.get(tipoOperacao.getCodigo()).stream().map(CaixaDetalhe::getValorDetalhe)
							.reduce(BigDecimal.ZERO, BigDecimal::add), BigDecimal.ZERO);
		}

		return BigDecimal.ZERO;
	}

	private BigDecimal getValorResumo(Map<String, List<ResumoCaixaMovimentoProjection>> resumoMovimentos, TipoSaldoEnum tipoSaldo) {

		if (resumoMovimentos.containsKey(tipoSaldo.getCodigo())) {

			return IfNull
					.get(resumoMovimentos.get(tipoSaldo.getCodigo()).stream().map(ResumoCaixaMovimentoProjection::getValorSaldo)
							.reduce(BigDecimal.ZERO, BigDecimal::add), BigDecimal.ZERO);
		}

		return BigDecimal.ZERO;
	}

	private void verificarCaixaUsuario(Usuario usuario) {

		Caixa caixa = caixaDAO.buscarCaixaPorUsuario(usuario.getIdUsuario());

		if (caixa == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_USUARIO_SEM_CAIXA);
		}
	}

	public BigDecimal buscarUltimoSaldoCaixa(Usuario usuario) {

		verificarCaixaUsuario(usuario);

		Caixa caixa = caixaDAO.buscarCaixaPorUsuario(usuario.getIdUsuario());

		if (caixa == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_USUARIO_SEM_CAIXA);
		}

		return IfNull.get(caixaSaldoDAO.buscarUltimoSaldoCaixa(caixa.getIdCaixa()), BigDecimal.ZERO);
	}

}
