package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QVeiculo.veiculo;

import java.util.List;

import br.com.lar.repository.model.Veiculo;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class VeiculoDAO extends PesquisableDAOImpl<Veiculo> {

	private static final long serialVersionUID = 1L;

	public VeiculoDAO() {
		super(veiculo, veiculo.idVeiculo);
	}

	public List<Veiculo> obterPorVersao(Long versaoRemota, Long versaoLocal, long quantidade) {

		return from().where(veiculo.sincronizacaoVersao.between(versaoRemota, versaoLocal)).orderBy(veiculo.sincronizacaoVersao.asc())
				.limit(quantidade).list(veiculo);
	}
}
