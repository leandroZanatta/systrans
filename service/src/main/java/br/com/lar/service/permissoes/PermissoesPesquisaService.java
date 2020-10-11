package br.com.lar.service.permissoes;

import java.util.List;

import br.com.sysdesc.pesquisa.repository.dao.impl.PermissaoPesquisaDAO;
import br.com.sysdesc.pesquisa.repository.model.PermissaoPesquisa;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;

public class PermissoesPesquisaService extends AbstractPesquisableServiceImpl<PermissaoPesquisa> {

	private PermissaoPesquisaDAO permissaoPesquisaDAO;

	public PermissoesPesquisaService() {
		this(new PermissaoPesquisaDAO());
	}

	public PermissoesPesquisaService(PermissaoPesquisaDAO permissaoPesquisaDAO) {
		super(permissaoPesquisaDAO, PermissaoPesquisa::getIdPermissaopesquisa);

		this.permissaoPesquisaDAO = permissaoPesquisaDAO;
	}

	public void excluir(List<PermissaoPesquisa> permissoesExcluir) {

		permissaoPesquisaDAO.getEntityManager().getTransaction().begin();

		permissoesExcluir.forEach(permissao -> permissaoPesquisaDAO.excluir(permissao));

		permissaoPesquisaDAO.getEntityManager().getTransaction().commit();
	}

	public List<PermissaoPesquisa> buscarPorPerfil(Long idPerfil) {

		return permissaoPesquisaDAO.buscarPorPerfil(idPerfil);
	}

	public List<PermissaoPesquisa> buscarPorUsuario(Long idUsuario) {

		return permissaoPesquisaDAO.buscarPorUsuario(idUsuario);
	}

}
