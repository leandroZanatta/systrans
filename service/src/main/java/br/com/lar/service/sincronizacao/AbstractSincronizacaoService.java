package br.com.lar.service.sincronizacao;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import br.com.sysdesc.util.enumeradores.SincronizacaoTabelaEnum;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractSincronizacaoService<T, K> implements Callable<Boolean> {

	private Long versaoLocal;
	private Long versaoRemota;
	private SincronizacaoTabelaEnum sincronizacaoTabelaEum;
	private Invocation.Builder endpoint;

	public AbstractSincronizacaoService(SincronizacaoTabelaEnum sincronizacaoTabelaEum, Invocation.Builder endpoint,
			List<SincronizacaoTabelaDTO> local, List<SincronizacaoTabelaDTO> remota) {
		this.sincronizacaoTabelaEum = sincronizacaoTabelaEum;
		this.endpoint = endpoint;
		this.versaoLocal = filter(local);
		this.versaoRemota = filter(remota);
	}

	@Override
	public Boolean call() throws Exception {

		log.info("Sincronizando tabela: {} - {} ~ {}", sincronizacaoTabelaEum, versaoLocal, versaoRemota);

		while (this.versaoRemota < this.versaoLocal) {

			try {

				List<T> listaSincronizar = obterPorVersaoBase(versaoRemota, versaoLocal, 500L);

				if (listaSincronizar.isEmpty()) {

					throw new RuntimeException("Erro ao sincronizar tabela: " + sincronizacaoTabelaEum);
				}

				Boolean retornoServer = this.endpoint.post(Entity.json(listaSincronizar.stream().map(this::mapToDTO).collect(Collectors.toList())),
						Boolean.class);

				if (retornoServer) {

					this.versaoRemota = listaSincronizar.stream().mapToLong(this::obterCodigoSincronizacao).max().getAsLong();
				}

			} catch (Exception e) {

				log.warn("Problemas de Sincronização: ", e);

				return false;
			}
		}

		return true;
	}

	private Long filter(List<SincronizacaoTabelaDTO> sincronizacoes) {

		return sincronizacoes.stream().filter(sincronizacao -> sincronizacao.getCodigoTabela().equals(sincronizacaoTabelaEum.getCodigo().longValue()))
				.map(SincronizacaoTabelaDTO::getVersaoSincronizacao).findFirst().orElse(0L);
	}

	protected abstract K mapToDTO(T objeto);

	protected abstract Long obterCodigoSincronizacao(T objeto);

	protected abstract List<T> obterPorVersaoBase(Long versaoRemota2, Long versaoLocal2, long l);
}
