package br.com.lar.service.cidade;

import java.util.List;

import br.com.lar.repository.dao.CidadeDAO;
import br.com.lar.repository.model.Cidade;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.constants.MensagemConstants;
import br.com.sysdesc.util.exception.SysDescException;

public class CidadeService extends AbstractPesquisableServiceImpl<Cidade> {

	private CidadeDAO cidadeDAO;

	public CidadeService() {
		this(new CidadeDAO());
	}

	public CidadeService(CidadeDAO cidadeDAO) {
		super(cidadeDAO, Cidade::getIdCidade);

		this.cidadeDAO = cidadeDAO;
	}

	@Override
	public void validar(Cidade objetoPersistir) {

		if (objetoPersistir.getEstado() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_ESTADO);
		}

		if (StringUtil.isNullOrEmpty(objetoPersistir.getDescricao())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DESCRICAO_VALIDA);
		}
	}

	public List<Cidade> buscarCidadesPorEstado(Long idEstado) {

		return cidadeDAO.buscarCidadePorEstado(idEstado);
	}
}
