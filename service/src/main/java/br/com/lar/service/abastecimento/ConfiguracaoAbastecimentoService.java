package br.com.lar.service.abastecimento;

import java.util.List;

import br.com.lar.repository.dao.ConfiguracaoAbastecimentoDAO;
import br.com.lar.repository.model.ConfiguracaoAbastecimento;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;

public class ConfiguracaoAbastecimentoService extends AbstractPesquisableServiceImpl<ConfiguracaoAbastecimento> {

	private ConfiguracaoAbastecimentoDAO configuracaoAbastecimentoDAO;

	public ConfiguracaoAbastecimentoService() {
		this(new ConfiguracaoAbastecimentoDAO());
	}

	public ConfiguracaoAbastecimentoService(ConfiguracaoAbastecimentoDAO configuracaoAbastecimentoDAO) {
		super(configuracaoAbastecimentoDAO, ConfiguracaoAbastecimento::getIdConfiguracaoAbastecimento);

		this.configuracaoAbastecimentoDAO = configuracaoAbastecimentoDAO;
	}

	public List<ConfiguracaoAbastecimento> buscarConfiguracaoPorVeiculo(Long codigoVeiculo) {

		return configuracaoAbastecimentoDAO.buscarConfiguracaoPorVeiculo(codigoVeiculo);
	}

}
