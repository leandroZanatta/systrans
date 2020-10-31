package br.com.lar.service.caixa;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.lar.repository.dao.CaixaCabecalhoDAO;
import br.com.lar.repository.dao.CaixaDetalheDAO;
import br.com.lar.repository.model.CaixaCabecalho;
import br.com.lar.repository.projection.ResumoCaixaDetalheProjection;
import br.com.lar.repository.projection.ResumoCaixaMovimentoProjection;
import br.com.lar.service.diario.DiarioService;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.sysdesc.util.classes.IfNull;
import br.com.sysdesc.util.enumeradores.TipoSaldoEnum;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum;
import br.com.systrans.util.vo.ResumoCaixaVO;

public class ResumoCaixaService {

	private DiarioService diarioService = new DiarioService();

	private CaixaDetalheDAO caixaDetalheDAO = new CaixaDetalheDAO();
	private CaixaCabecalhoDAO caixaCabecalhoDAO = new CaixaCabecalhoDAO();

	public ResumoCaixaVO obterResumoCaixa(Usuario usuario) {

		CaixaCabecalho cabecalho = caixaCabecalhoDAO.obterUltimoCaixaAberto(usuario.getIdUsuario());

		if (cabecalho == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_CAIXA_NAO_ENCONTRADO);
		}

		Map<Long, BigDecimal> mapaSaldo = caixaDetalheDAO.buscarResumoCaixa(cabecalho.getIdCaixaCabecalho()).stream()
				.collect(Collectors.toMap(ResumoCaixaDetalheProjection::getTipoSaldo, ResumoCaixaDetalheProjection::getValorSaldo));

		Map<String, BigDecimal> mapaReceitas = diarioService.buscarResumoCaixa(cabecalho.getIdCaixaCabecalho()).stream()
				.collect(Collectors.toMap(ResumoCaixaMovimentoProjection::getTipoSaldo, ResumoCaixaMovimentoProjection::getValorSaldo));

		return obterValorSaldo(cabecalho, mapaSaldo, mapaReceitas);
	}

	public ResumoCaixaVO obterResumoCaixaSemValorDinheiro(CaixaCabecalho caixaCabecalho) {

		Map<Long, BigDecimal> mapaSaldo = caixaDetalheDAO.buscarResumoCaixaSemValorDinheiro(caixaCabecalho.getIdCaixaCabecalho(), 4L).stream()
				.collect(Collectors.toMap(ResumoCaixaDetalheProjection::getTipoSaldo, ResumoCaixaDetalheProjection::getValorSaldo));

		Map<String, BigDecimal> mapaReceitas = diarioService.buscarResumoCaixa(caixaCabecalho.getIdCaixaCabecalho()).stream()
				.collect(Collectors.toMap(ResumoCaixaMovimentoProjection::getTipoSaldo, ResumoCaixaMovimentoProjection::getValorSaldo));

		return obterValorSaldo(caixaCabecalho, mapaSaldo, mapaReceitas);
	}

	private ResumoCaixaVO obterValorSaldo(CaixaCabecalho cabecalho, Map<Long, BigDecimal> mapaSaldo, Map<String, BigDecimal> mapaReceitas) {

		BigDecimal valorFaturamentoCredor = IfNull.get(mapaReceitas.get(TipoSaldoEnum.CREDOR.getCodigo()), BigDecimal.ZERO);
		BigDecimal valorFaturamentoDevedor = IfNull.get(mapaReceitas.get(TipoSaldoEnum.DEVEDOR.getCodigo()), BigDecimal.ZERO);

		BigDecimal valorSaldoCaixaCredor = IfNull.get(mapaSaldo.get(TipoHistoricoOperacaoEnum.CREDOR.getCodigo()), BigDecimal.ZERO);
		BigDecimal valorSaldoCaixaDevedor = IfNull.get(mapaSaldo.get(TipoHistoricoOperacaoEnum.DEVEDOR.getCodigo()), BigDecimal.ZERO);

		return new ResumoCaixaVO(cabecalho.getCaixa().getDescricao(), cabecalho.getDataMovimento(),
				valorFaturamentoCredor, valorFaturamentoDevedor, valorSaldoCaixaCredor, valorSaldoCaixaDevedor);
	}

}
