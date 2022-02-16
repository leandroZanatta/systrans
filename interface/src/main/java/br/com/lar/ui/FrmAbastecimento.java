package br.com.lar.ui;

import static br.com.sysdesc.util.resources.Resources.FRMABASTECIMENTO_TITLE;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.lar.service.login.LoginService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.ui.buttonactions.ButtonActionConfigurarServidor;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.classes.ContadorUtil;
import net.miginfocom.swing.MigLayout;

public class FrmAbastecimento extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;
	private JPanel painelContent;
	private LoginService loginService = new LoginService();
	private PanelActions<Usuario> panelActions;
	private ButtonActionConfigurarServidor configurarServidor;
	private JLabel lblUsuario;

	public FrmAbastecimento(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(450, 275);
		setClosable(Boolean.TRUE);
		setTitle(translate(FRMABASTECIMENTO_TITLE));

		painelContent = new JPanel();
		configurarServidor = new ButtonActionConfigurarServidor();

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][23.00][][23.00][][][grow]"));
		getContentPane().add(painelContent);

		lblUsuario = new JLabel("Usuário:");
		painelContent.add(lblUsuario, "cell 0 6");

		Action actionAlterarSenha = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				FrmApplication.getInstance().posicionarFrame(new FrmConfigurarIntegracaoAbastecimentos(getCodigoPrograma(), getCodigoUsuario()), null);
			}

		};

		panelActions = new PanelActions<Usuario>(this, loginService, PesquisaEnum.PES_USUARIOS.getCodigoPesquisa(), Boolean.FALSE,
				configurarServidor) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void posicionarBotoes() {

				ContadorUtil contadorUtil = new ContadorUtil();

				posicionarBotao(contadorUtil, configurarServidor, Boolean.TRUE);
				posicionarBotao(contadorUtil, btSalvar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btEditar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btNovo, Boolean.TRUE);
				posicionarBotao(contadorUtil, btBuscar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btCancelar, Boolean.TRUE);

			}

			@Override
			protected void registrarEventosBotoesPagina() {
				registrarEvento(configurarServidor, actionAlterarSenha);
			}

			@Override
			public void carregarObjeto(Usuario objeto) {

			}

			@Override
			public boolean preencherObjeto(Usuario objetoPesquisa) {

				return true;
			}

		};

		painelContent.add(panelActions, "cell 0 8,growx,aligny bottom");
	}

}
