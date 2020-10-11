package br.com.lar.service.estado;

import java.util.List;

import br.com.lar.repository.dao.EstadoDAO;
import br.com.lar.repository.model.Estado;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.constants.MensagemConstants;
import br.com.sysdesc.util.exception.SysDescException;

public class EstadoService extends AbstractPesquisableServiceImpl<Estado> {

	private final EstadoDAO estadoDAO;

	public EstadoService() {
		this(new EstadoDAO());
	}

	public EstadoService(EstadoDAO estadoDAO) {
		super(estadoDAO, Estado::getIdEstado);

		this.estadoDAO = estadoDAO;
	}

	@Override
	public void validar(Estado objetoPersistir) {

		if (StringUtil.isNullOrEmpty(objetoPersistir.getDescricao())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DESCRICAO_VALIDA);
		}

		if (StringUtil.isNullOrEmpty(objetoPersistir.getUf())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_UF_VALIDA);
		}

	}

	public List<Estado> listarEstados() {

		return estadoDAO.listar();
	}

}
