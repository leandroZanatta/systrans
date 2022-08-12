package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QAbastecimentoVeiculo.abastecimentoVeiculo;

import java.util.List;

import br.com.lar.repository.model.AbastecimentoVeiculo;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class AbastecimentoVeiculoDAO extends PesquisableDAOImpl<AbastecimentoVeiculo> {

	private static final long serialVersionUID = 1L;

	public AbastecimentoVeiculoDAO() {
		super(abastecimentoVeiculo, abastecimentoVeiculo.idAbastecimentoVeiculo);
	}

	public Long buscarUltimaKilometragemVeiculo(Long codigoVeiculo) {

		return from().where(abastecimentoVeiculo.configuracaoAbastecimento.codigoVeiculo.eq(codigoVeiculo))
				.singleResult(abastecimentoVeiculo.kilometroFinal.max());
	}

	public List<AbastecimentoVeiculo> buscarAbastecimentosUltimoTanqueCompleto(Long codigoVeiculo) {

		Long maiorIdAbastecimento = from().where(abastecimentoVeiculo.configuracaoAbastecimento.codigoVeiculo.eq(codigoVeiculo)
				.and(abastecimentoVeiculo.abastecimentoParcial.eq(false))).singleResult(abastecimentoVeiculo.idAbastecimentoVeiculo);

		if (maiorIdAbastecimento == null) {

			return null;
		}

		return from().where(abastecimentoVeiculo.configuracaoAbastecimento.codigoVeiculo.eq(codigoVeiculo)
				.and(abastecimentoVeiculo.idAbastecimentoVeiculo.gt(maiorIdAbastecimento))).list(abastecimentoVeiculo);
	}

}
