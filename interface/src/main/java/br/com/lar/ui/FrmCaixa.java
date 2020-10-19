package br.com.lar.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.lar.repository.model.Caixa;
import br.com.lar.service.caixa.CaixaService;
import br.com.lar.service.login.LoginService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import net.miginfocom.swing.MigLayout;

public class FrmCaixa extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JLabel lblCodigo;
	private JLabel lbUsuario;

	private JTextFieldId txCodigo;
	private CampoPesquisa<Usuario> pesquisaUsuario;
	private PanelActions<Caixa> panelActions;

	private LoginService loginService = new LoginService();
	private CaixaService caixaService = new CaixaService();
	private JTextFieldMaiusculo txDescricao;
	private JLabel lbDescricao;

	public FrmCaixa(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(500, 220);
		setClosable(Boolean.TRUE);
		setTitle("CADASTRO DE CAIXAS");

		painelContent = new JPanel();
		lblCodigo = new JLabel("Código:");
		lbUsuario = new JLabel("Usuário:");
		lbDescricao = new JLabel("Descrição:");

		txCodigo = new JTextFieldId();
		txDescricao = new JTextFieldMaiusculo();
		pesquisaUsuario = new CampoPesquisa<Usuario>(loginService, PesquisaEnum.PES_USUARIOS.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Usuario objeto) {
				return String.format("%d - %s", objeto.getIdUsuario(), objeto.getNomeUsuario());
			}
		};

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][30px:n][][][grow]"));

		painelContent.add(lblCodigo, "cell 0 0");
		painelContent.add(lbUsuario, "cell 0 2");
		painelContent.add(txDescricao, "cell 0 5,growx");
		painelContent.add(txCodigo, "cell 0 1,width 50:100:100");
		painelContent.add(pesquisaUsuario, "cell 0 3,grow");
		painelContent.add(lbDescricao, "cell 0 4");

		getContentPane().add(painelContent);

		panelActions = new PanelActions<Caixa>(this, caixaService,
				PesquisaEnum.PES_CAIXA.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(Caixa objeto) {

				txCodigo.setValue(objeto.getIdCaixa());
				pesquisaUsuario.setValue(objeto.getUsuario());
				txDescricao.setText(objeto.getDescricao());
			}

			@Override
			public boolean preencherObjeto(Caixa objetoPesquisa) {

				objetoPesquisa.setIdCaixa(txCodigo.getValue());
				objetoPesquisa.setUsuario(pesquisaUsuario.getObjetoPesquisado());
				objetoPesquisa.setDescricao(txDescricao.getText());

				return true;
			}

		};

		panelActions.addSaveListener(objeto -> txCodigo.setValue(objeto.getIdCaixa()));

		painelContent.add(panelActions, "cell 0 6,growx,aligny bottom");

	}

}
