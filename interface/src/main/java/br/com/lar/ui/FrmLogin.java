package br.com.lar.ui;

import static br.com.sysdesc.util.resources.Resources.FRMLOGIN_BT_CANCELAR;
import static br.com.sysdesc.util.resources.Resources.FRMLOGIN_BT_LOGAR;
import static br.com.sysdesc.util.resources.Resources.FRMLOGIN_LB_LOGIN;
import static br.com.sysdesc.util.resources.Resources.FRMLOGIN_LB_SENHA;
import static br.com.sysdesc.util.resources.Resources.FRMLOGIN_LB_USUARIO;
import static br.com.sysdesc.util.resources.Resources.FRMLOGIN_MSG_VERIFICACAO;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import br.com.lar.service.login.LoginService;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.exception.SysDescException;

public class FrmLogin extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel contentPanel;
	private JTextField txLogin;
	private JPasswordField txSenha;
	private JLabel lblLogin;
	private JLabel lblUsurio;
	private JLabel lblSenha;
	private JButton btLogin;
	private JButton btCancelar;
	private LoginService loginService = new LoginService();
	private Usuario usuario;

	public FrmLogin(JFrame parent) {
		super(parent);

		initComponents();
	}

	private void initComponents() {

		contentPanel = new JPanel();
		lblLogin = new JLabel(translate(FRMLOGIN_LB_LOGIN));
		lblUsurio = new JLabel(translate(FRMLOGIN_LB_USUARIO));
		lblSenha = new JLabel(translate(FRMLOGIN_LB_SENHA));
		txLogin = new JTextField();
		txSenha = new JPasswordField();
		btLogin = new JButton(translate(FRMLOGIN_BT_LOGAR));
		btCancelar = new JButton(translate(FRMLOGIN_BT_CANCELAR));

		lblLogin.setBounds(10, 26, 210, 14);
		lblUsurio.setBounds(10, 52, 93, 14);
		lblSenha.setBounds(10, 106, 210, 14);
		txLogin.setBounds(10, 73, 210, 20);
		txSenha.setBounds(10, 123, 210, 20);
		btLogin.setBounds(30, 169, 75, 23);
		btCancelar.setBounds(113, 169, 75, 23);

		btLogin.addActionListener(e -> efetuarLoginBtn());
		btCancelar.addActionListener(e -> fecharAplicacao());

		lblLogin.setHorizontalAlignment(SwingConstants.CENTER);

		contentPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPanel.setLayout(null);

		contentPanel.add(lblLogin);
		contentPanel.add(lblUsurio);
		contentPanel.add(lblSenha);
		contentPanel.add(txLogin);
		contentPanel.add(txSenha);
		contentPanel.add(btLogin);
		contentPanel.add(btCancelar);

		this.setSize(230, 250);
		this.setUndecorated(true);
		this.setModal(Boolean.TRUE);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(contentPanel, BorderLayout.CENTER);
		this.getRootPane().setDefaultButton(btLogin);

	}

	private void fecharAplicacao() {

		System.exit(0);
	}

	private void efetuarLoginBtn() {

		if (efetuarLogin()) {

			dispose();
		}
	}

	private Boolean efetuarLogin() {

		try {

			String senha = StringUtil.arrayToString(txSenha.getPassword());

			this.usuario = loginService.efetuarLogin(txLogin.getText(), senha);

			return true;

		} catch (SysDescException sysDescException) {

			JOptionPane.showMessageDialog(this, sysDescException.getMensagem(), translate(FRMLOGIN_MSG_VERIFICACAO),
					JOptionPane.INFORMATION_MESSAGE);
		}

		return false;

	}

	public Usuario getUsuario() {
		return usuario;
	}

}
