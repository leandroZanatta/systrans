package br.com.lar.service.funcionario;

import br.com.lar.repository.dao.FuncionarioDAO;
import br.com.lar.repository.dao.SalarioDAO;
import br.com.lar.repository.model.Funcionario;
import br.com.lar.repository.model.Salario;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.constants.MensagemConstants;
import br.com.sysdesc.util.exception.SysDescException;

public class FuncionarioService extends AbstractPesquisableServiceImpl<Funcionario> {

	private FuncionarioDAO funcionarioDAO;
	private SalarioDAO salarioDAO = new SalarioDAO();

	public FuncionarioService() {
		this(new FuncionarioDAO());
	}

	public FuncionarioService(FuncionarioDAO funcionarioDAO) {
		super(funcionarioDAO, Funcionario::getIdFuncionario);
		this.funcionarioDAO = funcionarioDAO;
	}

	@Override
	public void validar(Funcionario objetoPersistir) {

		if (objetoPersistir.getCliente() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_CLIENTE);
		}

		if (StringUtil.isNullOrEmpty(objetoPersistir.getCargo())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_CARGO);
		}

		if (objetoPersistir.getDataAdmissao() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DATA_ADMISSAO);
		}

		if (BigDecimalUtil.isNullOrZero(objetoPersistir.getSalario())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_SALARIO);
		}

		Funcionario funcionario = funcionarioDAO.buscarFuncionarioCadastrado(
				objetoPersistir.getCliente().getIdCliente(), objetoPersistir.getIdFuncionario());

		if (funcionario != null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_FUNCIONARIO_CADASTRADO);
		}
	}

	@Override
	public void salvar(Funcionario objetoPersistir) {

		boolean inclusaoFuncionario = LongUtil.isNullOrZero(objetoPersistir.getIdFuncionario());

		funcionarioDAO.salvar(objetoPersistir);

		if (inclusaoFuncionario) {

			Salario salario = new Salario();

			salario.setDataAlteracao(objetoPersistir.getDataAdmissao());
			salario.setFuncionario(objetoPersistir);
			salario.setValorSalario(objetoPersistir.getSalario());

			salarioDAO.salvar(salario);
		}
	}
}
