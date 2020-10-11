package br.com.lar.service.cliente;

import com.google.gson.Gson;

import br.com.lar.repository.dao.ClienteDAO;
import br.com.lar.repository.model.Cliente;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.CPFUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.constants.MensagemConstants;
import br.com.sysdesc.util.enumeradores.TipoClienteEnum;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.sysdesc.util.vo.CampoClientesHabilitadosVO;

public class ClienteService extends AbstractPesquisableServiceImpl<Cliente> {

	private ClienteDAO clienteDAO;

	public ClienteService() {
		this(new ClienteDAO());
	}

	public ClienteService(ClienteDAO clienteDAO) {
		super(clienteDAO, Cliente::getIdCliente);

		this.clienteDAO = clienteDAO;
	}

	@Override
	public void validar(Cliente objetoPersistir) {

		if (objetoPersistir.getGrupo() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_GRUPO);
		}

		if (StringUtil.isNullOrEmpty(objetoPersistir.getFlagTipoCliente())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_TIPO_CLIENTE);
		}

		boolean pessoaFisica = objetoPersistir.getFlagTipoCliente().equals(TipoClienteEnum.PESSOA_FISICA.getCodigo());

		CampoClientesHabilitadosVO campoClientes = new Gson().fromJson(objetoPersistir.getGrupo().getConfiguracao(),
				CampoClientesHabilitadosVO.class);

		if (pessoaFisica) {

			if (campoClientes.isCgc()
					&& StringUtil.isNullOrEmpty(StringUtil.formatarNumero(objetoPersistir.getCgc()))) {
				throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_CPF);
			}

			if (campoClientes.isCgc() && !CPFUtil.isCPFValido(objetoPersistir.getCgc())) {
				throw new SysDescException(MensagemConstants.MENSAGEM_CPF_INVALIDO);
			}

		} else {

			if (campoClientes.isCgc()
					&& StringUtil.isNullOrEmpty(StringUtil.formatarNumero(objetoPersistir.getCgc()))) {
				throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_CNPJ);
			}

			if (campoClientes.isCgc() && CPFUtil.isCPFValido(objetoPersistir.getCgc())) {
				throw new SysDescException(MensagemConstants.MENSAGEM_CNPJ_INVALIDO);
			}

		}

		if (campoClientes.isRg() && StringUtil.isNullOrEmpty(objetoPersistir.getRgie())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_RG);
		}

		if (campoClientes.isDataNascimento() && objetoPersistir.getDatadenascimento() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DATA_NASCIMENTO);
		}

		if (campoClientes.isNome() && StringUtil.isNullOrEmpty(objetoPersistir.getNome())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_NOME);
		}

		if (pessoaFisica && campoClientes.isNomePai() && StringUtil.isNullOrEmpty(objetoPersistir.getNomePai())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_NOME_PAI);
		}

		if (pessoaFisica && campoClientes.isNomeMae() && StringUtil.isNullOrEmpty(objetoPersistir.getNomeMae())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_NOME_MAE);
		}

		if (campoClientes.isCidade() && objetoPersistir.getCidade() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_CIDADE);
		}

		if (campoClientes.isEndereco() && StringUtil.isNullOrEmpty(objetoPersistir.getEndereco())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_ENDERECO);
		}

		if (campoClientes.isNumero() && StringUtil.isNullOrEmpty(objetoPersistir.getNumero())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_NUMERO);
		}

		if (pessoaFisica && campoClientes.isNaturalidade() && objetoPersistir.getNaturalidade() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_NATURALIDADE);
		}

		if (campoClientes.isBairro() && StringUtil.isNullOrEmpty(objetoPersistir.getBairro())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_BAIRRO);
		}

		if (campoClientes.isCep() && StringUtil.isNullOrEmpty(objetoPersistir.getCep())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_CEP);
		}

		if (campoClientes.isCelular() && objetoPersistir.getTelefone() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_CELULAR);
		}

		if (campoClientes.isEmail() && StringUtil.isNullOrEmpty(objetoPersistir.getEmail())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_EMAIL);
		}

		if (pessoaFisica && campoClientes.isEstadoCivil() && LongUtil.isNullOrZero(objetoPersistir.getEstadocivil())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_ESTADO_CIVIL);
		}

		if (pessoaFisica && campoClientes.isEscolaridade()
				&& LongUtil.isNullOrZero(objetoPersistir.getEscolaridade())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_ESCOLARIDADE);
		}

		if (pessoaFisica && campoClientes.isSexo() && StringUtil.isNullOrEmpty(objetoPersistir.getSexo())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_SEXO);
		}

		if (pessoaFisica && campoClientes.isRaca() && LongUtil.isNullOrZero(objetoPersistir.getNumeroCor())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_RACA);
		}

		if (pessoaFisica && campoClientes.isReligiao() && LongUtil.isNullOrZero(objetoPersistir.getReligiao())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_RELIGIAO);
		}

		if (pessoaFisica && campoClientes.isSituacao() && objetoPersistir.getSituacao() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_SITUACAO);
		}
	}

	public Cliente buscarClientePorCpf(String cgc, Long idCliente) {

		return clienteDAO.buscarClientePorCpf(cgc, idCliente);
	}

}
