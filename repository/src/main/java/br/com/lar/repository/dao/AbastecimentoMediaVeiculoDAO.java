package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QAbastecimentoMediaVeiculo.abastecimentoMediaVeiculo;

import java.util.List;

import br.com.lar.repository.model.AbastecimentoMediaVeiculo;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class AbastecimentoMediaVeiculoDAO extends PesquisableDAOImpl<AbastecimentoMediaVeiculo> {

	private static final long serialVersionUID = 1L;

	public AbastecimentoMediaVeiculoDAO() {
		super(abastecimentoMediaVeiculo, abastecimentoMediaVeiculo.idAbastecimentoMediaVeiculo);
	}

	public List<AbastecimentoMediaVeiculo> obterPorVersao(Long versaoRemota, Long versaoLocal, long quantidade) {

		return from().where(abastecimentoMediaVeiculo.sincronizacaoVersao.between(versaoRemota, versaoLocal))
				.orderBy(abastecimentoMediaVeiculo.sincronizacaoVersao.asc()).limit(quantidade).list(abastecimentoMediaVeiculo);
	}
}
