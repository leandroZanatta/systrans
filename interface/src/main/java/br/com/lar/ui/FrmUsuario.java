package br.com.lar.ui;

import static br.com.sysdesc.util.resources.Resources.FRMUSUARIO_LB_CODIGO;
import static br.com.sysdesc.util.resources.Resources.FRMUSUARIO_TITLE;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import br.com.lar.repository.model.Cliente;
import br.com.lar.service.cliente.ClienteService;
import br.com.lar.service.login.LoginService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.ui.buttonactions.ButtonActionAlterarSenha;
import br.com.lar.ui.dialogs.ValidarSenha;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.pesquisa.repository.model.Perfil;
import br.com.sysdesc.pesquisa.repository.model.PerfilUsuario;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.sysdesc.pesquisa.service.perfil.PerfilService;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisaMultiSelect;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.classes.ContadorUtil;
import br.com.sysdesc.util.classes.StringUtil;
import net.miginfocom.swing.MigLayout;

public class FrmUsuario extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;
	private JPanel painelContent;
	private JTextField txUsuario;
	private JLabel lblCliente;
	private JLabel lbPerfil;
	private JLabel lblCodigo;
	private JTextFieldId txCodigo;
	private PerfilService perfilService = new PerfilService();
	private PanelActions<Usuario> panelActions;
	private CampoPesquisaMultiSelect<Perfil> pesquisaPerfis;
	private LoginService loginService = new LoginService();
	private CampoPesquisa<Cliente> pesquisaCliente;
	private ClienteService clienteService = new ClienteService();
	private ButtonActionAlterarSenha alterarSenha;
	private JLabel lblUsuario;

	public FrmUsuario(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(450, 275);
		setClosable(Boolean.TRUE);
		setTitle(translate(FRMUSUARIO_TITLE));

		painelContent = new JPanel();
		lblCodigo = new JLabel(translate(FRMUSUARIO_LB_CODIGO));
		lblCliente = new JLabel("Cliente:");
		lbPerfil = new JLabel("Perfil:");
		txCodigo = new JTextFieldId();

		txUsuario = new JTextField();
		alterarSenha = new ButtonActionAlterarSenha();

		pesquisaCliente = new CampoPesquisa<Cliente>(clienteService, PesquisaEnum.PES_CLIENTES.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Cliente objeto) {
				return String.format("%d - %s", objeto.getIdCliente(), objeto.getNome());
			}
		};

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][23.00][][23.00][][][grow]"));
		getContentPane().add(painelContent);

		painelContent.add(lblCodigo, "cell 0 0");
		painelContent.add(txCodigo, "cell 0 1,width 50:100:100");
		painelContent.add(lblCliente, "cell 0 2");
		painelContent.add(pesquisaCliente, "cell 0 3,growx");
		painelContent.add(lbPerfil, "cell 0 4");

		pesquisaPerfis = new CampoPesquisaMultiSelect<Perfil>(perfilService,
				PesquisaEnum.PES_PERFIL.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected String formatarValorCampoMultiple(List<Perfil> objetosPesquisados) {

				return objetosPesquisados.stream().map(x -> x.getIdPerfil().toString())
						.collect(Collectors.joining(",", "<", ">"));
			}

			@Override
			protected String formatarValorCampoSingle(Perfil objeto) {

				return String.format("%s - %s", objeto.getIdPerfil(), objeto.getDescricao());
			}

		};
		painelContent.add(pesquisaPerfis, "cell 0 5,growx");

		lblUsuario = new JLabel("Usuário:");
		painelContent.add(lblUsuario, "cell 0 6");
		painelContent.add(txUsuario, "cell 0 7,growx");

		Action actionAlterarSenha = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				ValidarSenha validarSenha = new ValidarSenha();
				validarSenha.setVisible(Boolean.TRUE);

				if (!validarSenha.getOk()) {
					return;
				}

				Usuario usuario = panelActions.getObjetoPesquisa();

				usuario.setSenha(validarSenha.getSenha());

				loginService.salvar(usuario);

				JOptionPane.showMessageDialog(null, "SENHA ALTERADA COM SUCESSO.", "Verificação",
						JOptionPane.PLAIN_MESSAGE);
			}

		};

		panelActions = new PanelActions<Usuario>(this, loginService, PesquisaEnum.PES_USUARIOS.getCodigoPesquisa(),
				Boolean.FALSE, alterarSenha) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void posicionarBotoes() {

				ContadorUtil contadorUtil = new ContadorUtil();

				posicionarBotao(contadorUtil, alterarSenha, Boolean.TRUE);
				posicionarBotao(contadorUtil, btSalvar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btEditar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btNovo, Boolean.TRUE);
				posicionarBotao(contadorUtil, btBuscar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btCancelar, Boolean.TRUE);

			}

			@Override
			protected void registrarEventosBotoesPagina() {
				registrarEvento(alterarSenha, actionAlterarSenha);
			}

			@Override
			public void carregarObjeto(Usuario objeto) {

				txCodigo.setValue(objeto.getIdUsuario());
				txUsuario.setText(objeto.getNomeUsuario());
				pesquisaCliente.setValueById(objeto.getCodigoCliente());

				pesquisaPerfis.setValue(
						objeto.getPerfilUsuarios().stream().map(PerfilUsuario::getPerfil).collect(Collectors.toList()));
			}

			@Override
			public boolean preencherObjeto(Usuario objetoPesquisa) {

				objetoPesquisa.setIdUsuario(txCodigo.getValue());
				objetoPesquisa.setNomeUsuario(txUsuario.getText());
				objetoPesquisa.setCodigoCliente(null);

				if (pesquisaCliente.getObjetoPesquisado() != null) {

					objetoPesquisa.setCodigoCliente(pesquisaCliente.getObjetoPesquisado().getIdCliente());
				}
				pesquisaPerfis.getObjetosPesquisado().forEach(objetoPesquisa::addPerfilUsuario);

				if (StringUtil.isNullOrEmpty(objetoPesquisa.getSenha())) {

					ValidarSenha validarSenha = new ValidarSenha();
					validarSenha.setVisible(Boolean.TRUE);

					if (!validarSenha.getOk()) {
						return false;
					}

					objetoPesquisa.setSenha(validarSenha.getSenha());
				}

				return true;
			}

		};
		panelActions.addSaveListener(usuario -> txCodigo.setValue(usuario.getIdUsuario()));

		painelContent.add(panelActions, "cell 0 8,growx,aligny bottom");
	}

}
