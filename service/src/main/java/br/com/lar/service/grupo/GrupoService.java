package br.com.lar.service.grupo;

import br.com.lar.repository.dao.GrupoDAO;
import br.com.lar.repository.model.Grupo;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;

public class GrupoService extends AbstractPesquisableServiceImpl<Grupo> {

	public GrupoService() {
		super(new GrupoDAO(), Grupo::getIdGrupo);
	}

}
