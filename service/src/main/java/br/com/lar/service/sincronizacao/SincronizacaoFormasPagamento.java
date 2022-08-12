package br.com.lar.service.sincronizacao;

import static br.com.sysdesc.util.enumeradores.SincronizacaoTabelaEnum.FORMAS_PAGAMENTO;

import java.util.List;

import br.com.lar.repository.dao.FormasPagamentoDAO;
import br.com.lar.repository.model.FormasPagamento;
import br.com.systrans.util.dto.FormasPagamentoDTO;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;
import br.com.systrans.util.sincronizacao.ServerEndpoints;

public class SincronizacaoFormasPagamento extends AbstractSincronizacaoService<FormasPagamento, FormasPagamentoDTO> {

	private FormasPagamentoDAO formasPagamentoDAO = new FormasPagamentoDAO();

	public SincronizacaoFormasPagamento(List<SincronizacaoTabelaDTO> local, List<SincronizacaoTabelaDTO> remota) {
		super(FORMAS_PAGAMENTO, ServerEndpoints.getinstance().formasPagamento, local, remota);
	}

	@Override
	protected FormasPagamentoDTO mapToDTO(FormasPagamento formaPagamento) {
		return new FormasPagamentoDTO(formaPagamento.getIdFormaPagamento(), formaPagamento.getDescricao(), formaPagamento.getSincronizacaoVersao());
	}

	@Override
	protected Long obterCodigoSincronizacao(FormasPagamento objeto) {
		return objeto.getSincronizacaoVersao();
	}

	@Override
	protected List<FormasPagamento> obterPorVersaoBase(Long versaoLocal, Long versaoRemota, long limiteRegistros) {
		return formasPagamentoDAO.obterPorVersao(versaoLocal, versaoRemota, limiteRegistros);
	}

}
