package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QConfiguracaoAbastecimento.configuracaoAbastecimento;

import java.util.List;

import br.com.lar.repository.model.ConfiguracaoAbastecimento;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class ConfiguracaoAbastecimentoDAO extends PesquisableDAOImpl<ConfiguracaoAbastecimento> {

	private static final long serialVersionUID = 1L;

	public ConfiguracaoAbastecimentoDAO() {
		super(configuracaoAbastecimento, configuracaoAbastecimento.idConfiguracaoAbastecimento);
	}

	public List<ConfiguracaoAbastecimento> buscarConfiguracaoPorVeiculo(Long codigoVeiculo) {

		return from().where(configuracaoAbastecimento.codigoVeiculo.eq(codigoVeiculo)).list(configuracaoAbastecimento);
	}

	public List<ConfiguracaoAbastecimento> obterPorVersao(Long versaoLocal, Long versaoRemota, long limite) {

		return from().where(configuracaoAbastecimento.sincronizacaoVersao.between(versaoLocal, versaoRemota)).limit(limite)
				.list(configuracaoAbastecimento);
	}

}
