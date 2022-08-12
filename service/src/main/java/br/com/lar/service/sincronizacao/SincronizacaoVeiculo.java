package br.com.lar.service.sincronizacao;

import static br.com.sysdesc.util.enumeradores.SincronizacaoTabelaEnum.VEICULOS;

import java.util.List;

import br.com.lar.repository.dao.VeiculoDAO;
import br.com.lar.repository.model.Veiculo;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;
import br.com.systrans.util.dto.VeiculoDTO;
import br.com.systrans.util.sincronizacao.ServerEndpoints;

public class SincronizacaoVeiculo extends AbstractSincronizacaoService<Veiculo, VeiculoDTO> {

	private VeiculoDAO veiculoDAO = new VeiculoDAO();

	public SincronizacaoVeiculo(List<SincronizacaoTabelaDTO> local, List<SincronizacaoTabelaDTO> remota) {
		super(VEICULOS, ServerEndpoints.getinstance().veiculos, local, remota);
	}

	@Override
	protected VeiculoDTO mapToDTO(Veiculo veiculo) {
		return new VeiculoDTO(veiculo.getIdVeiculo(), veiculo.getPlaca(), veiculo.getSincronizacaoVersao());
	}

	@Override
	protected Long obterCodigoSincronizacao(Veiculo objeto) {
		return objeto.getSincronizacaoVersao();
	}

	@Override
	protected List<Veiculo> obterPorVersaoBase(Long versaoLocal, Long versaoRemota, long limiteRegistros) {
		return veiculoDAO.obterPorVersao(versaoLocal, versaoRemota, limiteRegistros);
	}

}
