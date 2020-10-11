package br.com.lar.service.permissoes;

import java.util.List;

import br.com.lar.repository.dao.PermissaoProgramaDAO;
import br.com.lar.repository.model.PermissaoPrograma;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;

public class PermissoesProgramaService extends AbstractPesquisableServiceImpl<PermissaoPrograma> {

	private PermissaoProgramaDAO permissaoProgramaDAO;

	public PermissoesProgramaService() {
		this(new PermissaoProgramaDAO());
	}

	public PermissoesProgramaService(PermissaoProgramaDAO permissaoProgramaDAO) {
		super(permissaoProgramaDAO, PermissaoPrograma::getIdPermissaoprograma);

		this.permissaoProgramaDAO = permissaoProgramaDAO;
	}

	public void salvarTodos(List<PermissaoPrograma> permissoes) {

		permissaoProgramaDAO.salvar(permissoes);
	}

	public List<PermissaoPrograma> buscarPorPerfil(Long idPerfil) {

		return permissaoProgramaDAO.buscarPorPerfil(idPerfil);
	}

	public List<PermissaoPrograma> buscarPorUsuario(Long idUsuario) {

		return permissaoProgramaDAO.buscarPorUsuario(idUsuario);
	}

}
