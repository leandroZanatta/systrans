package br.com.lar.service.programa;

import java.util.List;

import br.com.lar.repository.dao.ProgramaDAO;
import br.com.lar.repository.model.Programa;

public class ProgramaService {

	private ProgramaDAO programaDAO = new ProgramaDAO();

	public List<Programa> buscarRootProgramas() {

		return programaDAO.buscarRootProgramas();
	}

	public List<Programa> buscarSubMenus(Long idPrograma) {

		return programaDAO.buscarSubMenus(idPrograma);
	}
}
