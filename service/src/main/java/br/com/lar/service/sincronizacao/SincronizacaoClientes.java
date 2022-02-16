package br.com.lar.service.sincronizacao;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import br.com.lar.repository.dao.ClienteDAO;
import br.com.lar.repository.model.Cliente;
import br.com.systrans.util.dto.ClienteDTO;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;
import br.com.systrans.util.sincronizacao.ServerEndpoints;
import jakarta.ws.rs.client.Entity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SincronizacaoClientes implements Callable<Boolean> {

	private Long versaoLocal;
	private Long versaoRemota;
	private ClienteDAO clienteDAO = new ClienteDAO();

	public SincronizacaoClientes(List<SincronizacaoTabelaDTO> local, List<SincronizacaoTabelaDTO> remota) {

		this.versaoLocal = local.stream().filter(sincronizacao -> sincronizacao.getCodigoTabela().equals(2L))
				.map(SincronizacaoTabelaDTO::getVersaoSincronizacao).findFirst().orElse(0L);
		this.versaoRemota = remota.stream().filter(sincronizacao -> sincronizacao.getCodigoTabela().equals(2L))
				.map(SincronizacaoTabelaDTO::getVersaoSincronizacao).findFirst().orElse(0L);
	}

	@Override
	public Boolean call() throws Exception {

		while (this.versaoRemota < this.versaoLocal) {

			try {

				List<Cliente> clientesSincronizar = clienteDAO.obterPorVersao(versaoRemota, versaoLocal, 500L);

				Boolean retornoServer = ServerEndpoints.getinstance().clientes.post(Entity.json(clientesSincronizar.stream()
						.map(cliente -> new ClienteDTO(cliente.getIdCliente(), cliente.getNome(), cliente.getCgc(), cliente.getSincronizacaoVersao()))
						.collect(Collectors.toList())), Boolean.class);

				if (retornoServer) {

					this.versaoRemota = clientesSincronizar.stream().mapToLong(Cliente::getSincronizacaoVersao).max().getAsLong();
				}

			} catch (Exception e) {

				log.warn("Ocorreu um erro ao efetuar sincronização de clientes", e);
			}
		}

		return true;
	}

}
