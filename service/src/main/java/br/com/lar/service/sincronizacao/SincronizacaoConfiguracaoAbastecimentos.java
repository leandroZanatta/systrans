package br.com.lar.service.sincronizacao;

import static br.com.sysdesc.util.enumeradores.SincronizacaoTabelaEnum.CONFIGURACOES_ABASTECIMENTOS;

import java.util.List;

import br.com.lar.repository.dao.ConfiguracaoAbastecimentoDAO;
import br.com.lar.repository.model.ConfiguracaoAbastecimento;
import br.com.systrans.util.dto.ConfiguracaoAbastecimentoDTO;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;
import br.com.systrans.util.sincronizacao.ServerEndpoints;

public class SincronizacaoConfiguracaoAbastecimentos extends AbstractSincronizacaoService<ConfiguracaoAbastecimento, ConfiguracaoAbastecimentoDTO> {

	private ConfiguracaoAbastecimentoDAO configuracaoAbastecimentoDAO = new ConfiguracaoAbastecimentoDAO();

	public SincronizacaoConfiguracaoAbastecimentos(List<SincronizacaoTabelaDTO> local, List<SincronizacaoTabelaDTO> remota) {
		super(CONFIGURACOES_ABASTECIMENTOS, ServerEndpoints.getinstance().configuracaoAbastecimento, local, remota);
	}

	@Override
	protected ConfiguracaoAbastecimentoDTO mapToDTO(ConfiguracaoAbastecimento configuracao) {
		return new ConfiguracaoAbastecimentoDTO(configuracao.getIdConfiguracaoAbastecimento(), configuracao.getCodigoVeiculo(),
				configuracao.getOperacao().getHistorico().getDescricao(), configuracao.getOperacao().getCodigoFormaPagamento(),
				configuracao.getSincronizacaoVersao());
	}

	@Override
	protected Long obterCodigoSincronizacao(ConfiguracaoAbastecimento objeto) {
		return objeto.getSincronizacaoVersao();
	}

	@Override
	protected List<ConfiguracaoAbastecimento> obterPorVersaoBase(Long versaoLocal, Long versaoRemota, long limiteRegistros) {

		return configuracaoAbastecimentoDAO.obterPorVersao(versaoLocal, versaoRemota, limiteRegistros);
	}

}
