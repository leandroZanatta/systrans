package br.com.lar.service.sincronizacao;

import static br.com.sysdesc.util.enumeradores.SincronizacaoTabelaEnum.CLIENTES;

import java.util.List;

import br.com.lar.repository.dao.ClienteDAO;
import br.com.lar.repository.model.Cliente;
import br.com.systrans.util.dto.ClienteDTO;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;
import br.com.systrans.util.sincronizacao.ServerEndpoints;

public class SincronizacaoClientes extends AbstractSincronizacaoService<Cliente, ClienteDTO> {

	private ClienteDAO clienteDAO = new ClienteDAO();

	public SincronizacaoClientes(List<SincronizacaoTabelaDTO> local, List<SincronizacaoTabelaDTO> remota) {
		super(CLIENTES, ServerEndpoints.getinstance().clientes, local, remota);
	}

	@Override
	protected ClienteDTO mapToDTO(Cliente cliente) {
		return new ClienteDTO(cliente.getIdCliente(), cliente.getNome(), cliente.getCgc(), cliente.getSituacao(), cliente.getSincronizacaoVersao());
	}

	@Override
	protected Long obterCodigoSincronizacao(Cliente objeto) {
		return objeto.getSincronizacaoVersao();
	}

	@Override
	protected List<Cliente> obterPorVersaoBase(Long versaoLocal, Long versaoRemota, long limiteRegistros) {
		return clienteDAO.obterPorVersao(versaoLocal, versaoRemota, limiteRegistros);
	}
}
