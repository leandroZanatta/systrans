package br.com.lar.service.sincronizacao;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.gson.reflect.TypeToken;

import br.com.lar.repository.dao.SincronizacaoItemDAO;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;
import br.com.systrans.util.sincronizacao.ServerEndpoints;
import jakarta.ws.rs.core.GenericType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SincronizacaoService {

	private SincronizacaoItemDAO sincronizacaoItemDAO = new SincronizacaoItemDAO();
	private ExecutorService executorService = Executors.newCachedThreadPool();

	public Boolean servidorAtivo() {
		try {

			return ServerEndpoints.getinstance().servidorAtivo.get(Boolean.class);

		} catch (Exception e) {
			return false;
		}
	}

	public synchronized void iniciarSincronizacao() {

		if (servidorAtivo()) {

			List<SincronizacaoTabelaDTO> dadosLocais = sincronizacaoItemDAO.obterDadosSincronizacao();

			List<SincronizacaoTabelaDTO> dadosRemotos = ServerEndpoints.getinstance().obterVersoes.get(
					new GenericType<List<SincronizacaoTabelaDTO>>(TypeToken.getParameterized(List.class, SincronizacaoTabelaDTO.class).getType()));

			try {

				List<Future<Boolean>> futures = executorService.invokeAll(Arrays.asList(new SincronizacaoVeiculo(dadosLocais, dadosRemotos),
						new SincronizacaoFormasPagamento(dadosLocais, dadosRemotos), new SincronizacaoUsuarios(dadosLocais, dadosRemotos),
						new SincronizacaoClientes(dadosLocais, dadosRemotos), new SincronizacaoConfiguracaoAbastecimentos(dadosLocais, dadosRemotos),
						new SincronizacaoAbastecimentosMediaVeiculos(dadosLocais, dadosRemotos)));

				for (Future<Boolean> future : futures) {

					if (!future.isDone()) {
						future.get();
					}
				}

			} catch (InterruptedException | ExecutionException e) {

				log.error("Erro realizando sincronização.", e);
			}
		} else {
			log.warn("Não foi possível efetuar a sincronização, servidor não está ativo");
		}
	}
}