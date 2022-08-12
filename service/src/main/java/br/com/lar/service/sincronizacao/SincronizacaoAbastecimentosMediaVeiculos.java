package br.com.lar.service.sincronizacao;

import static br.com.sysdesc.util.enumeradores.SincronizacaoTabelaEnum.MEDIAS_VEICULOS;

import java.util.List;

import br.com.lar.repository.dao.AbastecimentoMediaVeiculoDAO;
import br.com.lar.repository.model.AbastecimentoMediaVeiculo;
import br.com.systrans.util.dto.AbastecimentoMediaVeiculoDTO;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;
import br.com.systrans.util.sincronizacao.ServerEndpoints;

public class SincronizacaoAbastecimentosMediaVeiculos extends AbstractSincronizacaoService<AbastecimentoMediaVeiculo, AbastecimentoMediaVeiculoDTO> {

	private AbastecimentoMediaVeiculoDAO abstecimentoMediaVeiculoDAO = new AbastecimentoMediaVeiculoDAO();

	public SincronizacaoAbastecimentosMediaVeiculos(List<SincronizacaoTabelaDTO> local, List<SincronizacaoTabelaDTO> remota) {
		super(MEDIAS_VEICULOS, ServerEndpoints.getinstance().abastecimentoMediaVeiculo, local, remota);
	}

	@Override
	protected AbastecimentoMediaVeiculoDTO mapToDTO(AbastecimentoMediaVeiculo cliente) {
		return new AbastecimentoMediaVeiculoDTO(cliente.getIdAbastecimentoMediaVeiculo(), cliente.getVeiculo().getIdVeiculo(),
				cliente.getDataCalculo(), cliente.getQuantidadeKilometros(), cliente.getLitrosAbastecidos(), cliente.getValorAbastecimento(),
				cliente.getKilometrosPorLitro(), cliente.getReaisPorKilometro(), cliente.getSincronizacaoVersao());
	}

	@Override
	protected Long obterCodigoSincronizacao(AbastecimentoMediaVeiculo objeto) {
		return objeto.getSincronizacaoVersao();
	}

	@Override
	protected List<AbastecimentoMediaVeiculo> obterPorVersaoBase(Long versaoLocal, Long versaoRemota, long limiteRegistros) {
		return abstecimentoMediaVeiculoDAO.obterPorVersao(versaoLocal, versaoRemota, limiteRegistros);
	}
}
