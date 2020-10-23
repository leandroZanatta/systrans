package br.com.lar.service.motorista;

import br.com.lar.repository.dao.MotoristaDAO;
import br.com.lar.repository.model.Motorista;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class MotoristaService extends AbstractPesquisableServiceImpl<Motorista> {

	private MotoristaDAO motoristaDAO;

	public MotoristaService() {
		this(new MotoristaDAO());
	}

	public MotoristaService(MotoristaDAO motoristaDAO) {
		super(motoristaDAO, Motorista::getIdMotorista);

		this.motoristaDAO = motoristaDAO;
	}

	@Override
	public void validar(Motorista objetoPersistir) {

		if (objetoPersistir.getFuncionario() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_FUNCIONARIO);
		}

		if (StringUtil.isNullOrEmpty(objetoPersistir.getCnh())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_CNH);
		}

		if (LongUtil.isNullOrZero(objetoPersistir.getNumeroDocumento())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_NUMERO_DOCUMENTO);
		}

		if (objetoPersistir.getDataVencimento() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DATA_VENCIMENTO);
		}

		Motorista motorista = motoristaDAO.buscarMotoristaCadastrado(objetoPersistir.getFuncionario().getIdFuncionario(),
				objetoPersistir.getIdMotorista());

		if (motorista != null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_MOTORISTA_CADASTRADO);
		}
	}
}
