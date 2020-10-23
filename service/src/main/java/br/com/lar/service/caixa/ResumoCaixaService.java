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
import br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum;
import br.com.systrans.util.vo.ResumoCaixaVO;

public class ResumoCaixaService {

	private DiarioService diarioService = new DiarioService();

	private CaixaDetalheDAO caixaDetalheDAO = new CaixaDetalheDAO();
	private CaixaCabecalhoDAO caixaCabecalhoDAO = new CaixaCabecalhoDAO();

	public ResumoCaixaVO obterResumoCaixa(Usuario usuario) {

		CaixaCabecalho cabecalho = caixaCabecalhoDAO.obterCaixa(usuario.getIdUsuario());

		Map<Long, BigDecimal> mapaSaldo = caixaDetalheDAO.buscarResumoCaixa(cabecalho.getIdCaixaCabecalho()).stream()
				.collect(Collectors.toMap(ResumoCaixaDetalheProjection::getTipoSaldo, ResumoCaixaDetalheProjection::getValorSaldo));

		Map<String, BigDecimal> mapaReceitas = diarioService.buscarResumoCaixa(cabecalho.getIdCaixaCabecalho()).stream()
				.collect(Collectors.toMap(ResumoCaixaMovimentoProjection::getTipoSaldo, ResumoCaixaMovimentoProjection::getValorSaldo));

		BigDecimal valorFaturamento = IfNull.get(mapaReceitas.get(TipoSaldoEnum.CREDOR.getCodigo()), BigDecimal.ZERO);
		BigDecimal valorSaldoCaixaCredor = IfNull.get(mapaSaldo.get(TipoHistoricoOperacaoEnum.CREDOR.getCodigo()), BigDecimal.ZERO);

		return new ResumoCaixaVO(valorFaturamento, valorSaldoCaixaCredor);
	}

	public ResumoCaixaVO obterResumoCaixaSemValorDinheiro(CaixaCabecalho caixaCabecalho) {

		Map<Long, BigDecimal> mapaSaldo = caixaDetalheDAO.buscarResumoCaixaSemValorDinheiro(caixaCabecalho.getIdCaixaCabecalho(), 4L).stream()
				.collect(Collectors.toMap(ResumoCaixaDetalheProjection::getTipoSaldo, ResumoCaixaDetalheProjection::getValorSaldo));

		Map<String, BigDecimal> mapaReceitas = diarioService.buscarResumoCaixa(caixaCabecalho.getIdCaixaCabecalho()).stream()
				.collect(Collectors.toMap(ResumoCaixaMovimentoProjection::getTipoSaldo, ResumoCaixaMovimentoProjection::getValorSaldo));

		BigDecimal valorFaturamento = IfNull.get(mapaReceitas.get(TipoSaldoEnum.CREDOR.getCodigo()), BigDecimal.ZERO);
		BigDecimal valorSaldoCaixaCredor = IfNull.get(mapaSaldo.get(TipoHistoricoOperacaoEnum.CREDOR.getCodigo()), BigDecimal.ZERO);

		return new ResumoCaixaVO(valorFaturamento, valorSaldoCaixaCredor);
	}

	public BigDecimal calcularSaldoCaixa(Long codigoCaixa) {

		Map<Long, BigDecimal> mapaSaldo = caixaDetalheDAO.buscarResumoCaixa(codigoCaixa).stream()
				.collect(Collectors.toMap(ResumoCaixaDetalheProjection::getTipoSaldo, ResumoCaixaDetalheProjection::getValorSaldo));

		BigDecimal valorSaldoCaixaCredor = IfNull.get(mapaSaldo.get(TipoHistoricoOperacaoEnum.CREDOR.getCodigo()), BigDecimal.ZERO);
		BigDecimal valorSaldoCaixaDevedor = IfNull.get(mapaSaldo.get(TipoHistoricoOperacaoEnum.DEVEDOR.getCodigo()), BigDecimal.ZERO);

		return valorSaldoCaixaCredor.subtract(valorSaldoCaixaDevedor);
	}

}
