package br.com.lar.service.sincronizacao;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import br.com.lar.repository.dao.VeiculoDAO;
import br.com.lar.repository.model.Veiculo;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;
import br.com.systrans.util.dto.VeiculoDTO;
import br.com.systrans.util.sincronizacao.ServerEndpoints;
import jakarta.ws.rs.client.Entity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SincronizacaoVeiculo implements Callable<Boolean> {

	private Long versaoLocal;
	private Long versaoRemota;
	private VeiculoDAO veiculoDAO = new VeiculoDAO();

	public SincronizacaoVeiculo(List<SincronizacaoTabelaDTO> local, List<SincronizacaoTabelaDTO> remota) {

		this.versaoLocal = local.stream().filter(sincronizacao -> sincronizacao.getCodigoTabela().equals(3L))
				.map(SincronizacaoTabelaDTO::getVersaoSincronizacao).findFirst().orElse(0L);
		this.versaoRemota = remota.stream().filter(sincronizacao -> sincronizacao.getCodigoTabela().equals(3L))
				.map(SincronizacaoTabelaDTO::getVersaoSincronizacao).findFirst().orElse(0L);
	}

	@Override
	public Boolean call() throws Exception {

		while (this.versaoRemota < this.versaoLocal) {

			try {

				List<Veiculo> veiculosSincronizar = veiculoDAO.obterPorVersao(versaoRemota, versaoLocal, 500L);

				Boolean retornoServer = ServerEndpoints.getinstance().veiculos.post(Entity.json(veiculosSincronizar.stream()
						.map(veiculo -> new VeiculoDTO(veiculo.getIdVeiculo(), veiculo.getPlaca(), veiculo.getSincronizacaoVersao()))
						.collect(Collectors.toList())), Boolean.class);

				if (retornoServer) {

					this.versaoRemota = veiculosSincronizar.stream().mapToLong(Veiculo::getSincronizacaoVersao).max().getAsLong();
				}

			} catch (Exception e) {

				log.warn("Ocorreu um erro ao efetuar sincronização de veiculos", e);
			}
		}

		return true;
	}

}
