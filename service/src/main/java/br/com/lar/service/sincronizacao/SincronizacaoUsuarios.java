package br.com.lar.service.sincronizacao;

import static br.com.sysdesc.util.enumeradores.SincronizacaoTabelaEnum.USUARIOS;

import java.util.List;

import br.com.sysdesc.pesquisa.repository.dao.impl.UsuarioDAO;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;
import br.com.systrans.util.dto.UsuarioDTO;
import br.com.systrans.util.sincronizacao.ServerEndpoints;

public class SincronizacaoUsuarios extends AbstractSincronizacaoService<Usuario, UsuarioDTO> {

	private UsuarioDAO usuarioDAO = new UsuarioDAO();

	public SincronizacaoUsuarios(List<SincronizacaoTabelaDTO> local, List<SincronizacaoTabelaDTO> remota) {
		super(USUARIOS, ServerEndpoints.getinstance().usuarios, local, remota);
	}

	@Override
	protected UsuarioDTO mapToDTO(Usuario usuario) {
		return new UsuarioDTO(usuario.getIdUsuario(), usuario.getNomeUsuario(), usuario.getSenha(), usuario.getSincronizacaoVersao());
	}

	@Override
	protected Long obterCodigoSincronizacao(Usuario objeto) {
		return objeto.getSincronizacaoVersao();
	}

	@Override
	protected List<Usuario> obterPorVersaoBase(Long versaoLocal, Long versaoRemota, long limiteRegistros) {
		return usuarioDAO.obterPorVersao(versaoLocal, versaoRemota, limiteRegistros);
	}

}
