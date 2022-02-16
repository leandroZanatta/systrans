package br.com.lar.service.sincronizacao;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import br.com.sysdesc.pesquisa.repository.dao.impl.UsuarioDAO;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;
import br.com.systrans.util.dto.UsuarioDTO;
import br.com.systrans.util.sincronizacao.ServerEndpoints;
import jakarta.ws.rs.client.Entity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SincronizacaoUsuarios implements Callable<Boolean> {

	private Long versaoLocal;
	private Long versaoRemota;
	private UsuarioDAO usuarioDAO = new UsuarioDAO();

	public SincronizacaoUsuarios(List<SincronizacaoTabelaDTO> local, List<SincronizacaoTabelaDTO> remota) {

		this.versaoLocal = local.stream().filter(sincronizacao -> sincronizacao.getCodigoTabela().equals(1L))
				.map(SincronizacaoTabelaDTO::getVersaoSincronizacao).findFirst().orElse(0L);
		this.versaoRemota = remota.stream().filter(sincronizacao -> sincronizacao.getCodigoTabela().equals(1L))
				.map(SincronizacaoTabelaDTO::getVersaoSincronizacao).findFirst().orElse(0L);
	}

	@Override
	public Boolean call() throws Exception {

		while (this.versaoRemota < this.versaoLocal) {

			try {

				List<Usuario> usuariosSincronizar = usuarioDAO.obterPorVersao(versaoRemota, versaoLocal, 500L);

				Boolean retornoServer = ServerEndpoints.getinstance().usuarios
						.post(Entity.json(usuariosSincronizar.stream().map(usuario -> new UsuarioDTO(usuario.getIdUsuario(), usuario.getNomeUsuario(),
								usuario.getSenha(), usuario.getSincronizacaoVersao())).collect(Collectors.toList())), Boolean.class);

				if (retornoServer) {

					this.versaoRemota = usuariosSincronizar.stream().mapToLong(Usuario::getSincronizacaoVersao).max().getAsLong();
				}

			} catch (Exception e) {

				log.warn("Ocorreu um erro ao efetuar sincronização de usuários", e);
			}
		}

		return true;
	}

}
