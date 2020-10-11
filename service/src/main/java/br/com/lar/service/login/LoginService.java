package br.com.lar.service.login;

import static br.com.sysdesc.util.resources.Resources.FRMLOGIN_MSG_LOGIN;
import static br.com.sysdesc.util.resources.Resources.FRMLOGIN_MSG_SENHA;
import static br.com.sysdesc.util.resources.Resources.FRMLOGIN_MSG_USUARIO;

import br.com.sysdesc.pesquisa.repository.dao.impl.UsuarioDAO;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.CryptoUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.constants.MensagemConstants;
import br.com.sysdesc.util.exception.SysDescException;

public class LoginService extends AbstractPesquisableServiceImpl<Usuario> {

	private final UsuarioDAO usuarioDAO;

	public LoginService() {
		this(new UsuarioDAO());
	}

	public LoginService(UsuarioDAO usuarioDAO) {
		super(usuarioDAO, Usuario::getIdUsuario);

		this.usuarioDAO = usuarioDAO;
	}

	public Usuario efetuarLogin(String usuario, String senha) {

		if (StringUtil.isNullOrEmpty(usuario)) {

			throw new SysDescException(FRMLOGIN_MSG_USUARIO);

		}
		if (StringUtil.isNullOrEmpty(senha)) {

			throw new SysDescException(FRMLOGIN_MSG_SENHA);

		}

		Usuario mdlUsuario = usuarioDAO.obterLogin(usuario, CryptoUtil.toMD5(senha));

		if (mdlUsuario == null) {

			throw new SysDescException(FRMLOGIN_MSG_LOGIN);
		}

		return mdlUsuario;
	}

	@Override
	public void validar(Usuario objetoPersistir) {

		if (StringUtil.isNullOrEmpty(objetoPersistir.getNomeUsuario())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_USUARIO);

		}

		if (LongUtil.isNullOrZero(objetoPersistir.getCodigoCliente())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_CLIENTE);

		}

		if (usuarioDAO.verificarUsuarioJaCadastrado(objetoPersistir.getCodigoCliente(),
				objetoPersistir.getIdUsuario())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_CLIENTE_COM_LOGIN);

		}

	}

}
