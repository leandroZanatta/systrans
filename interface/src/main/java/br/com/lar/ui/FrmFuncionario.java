package br.com.lar.ui;

import static br.com.sysdesc.util.resources.Resources.FRMFUNCIONARIO_LB_ADMISSAO;
import static br.com.sysdesc.util.resources.Resources.FRMFUNCIONARIO_LB_CARGO;
import static br.com.sysdesc.util.resources.Resources.FRMFUNCIONARIO_LB_CLIENTE;
import static br.com.sysdesc.util.resources.Resources.FRMFUNCIONARIO_LB_CODIGO;
import static br.com.sysdesc.util.resources.Resources.FRMFUNCIONARIO_LB_DEMISSAO;
import static br.com.sysdesc.util.resources.Resources.FRMFUNCIONARIO_LB_SALARIO;
import static br.com.sysdesc.util.resources.Resources.FRMFUNCIONARIO_TITLE;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.Funcionario;
import br.com.lar.service.cliente.ClienteService;
import br.com.lar.service.funcionario.FuncionarioService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.ui.buttonactions.ButtonActionAlterarSalario;
import br.com.lar.ui.dialogs.FrmAumentoSalarial;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.components.listeners.adapter.ButtonActionListenerAdapter;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.classes.ContadorUtil;
import net.miginfocom.swing.MigLayout;

public class FrmFuncionario extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JLabel lblCodigo;
	private JLabel lblCliente;
	private JLabel lblAdmisso;
	private JLabel lblDemissao;
	private JLabel lblSalario;

	private JTextFieldId txCodigo;
	private CampoPesquisa<Cliente> pesquisaCliente;
	private JDateChooser dataAdmissao;
	private JDateChooser dataDemissao;
	private JMoneyField txSalario;

	private JPanel painelContent;
	private PanelActions<Funcionario> panelActions;

	private ClienteService clienteService = new ClienteService();
	private FuncionarioService funcionarioService = new FuncionarioService();
	private JLabel lblCargo;
	private JTextFieldMaiusculo txCargo;
	private ButtonActionAlterarSalario alterarSalario;

	public FrmFuncionario(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(500, 265);
		setClosable(Boolean.TRUE);
		setTitle(translate(FRMFUNCIONARIO_TITLE));

		painelContent = new JPanel();
		lblCodigo = new JLabel(translate(FRMFUNCIONARIO_LB_CODIGO));
		lblDemissao = new JLabel(translate(FRMFUNCIONARIO_LB_DEMISSAO));
		lblCliente = new JLabel(translate(FRMFUNCIONARIO_LB_CLIENTE));
		lblSalario = new JLabel(translate(FRMFUNCIONARIO_LB_SALARIO));
		lblAdmisso = new JLabel(translate(FRMFUNCIONARIO_LB_ADMISSAO));
		lblCargo = new JLabel(translate(FRMFUNCIONARIO_LB_CARGO));

		alterarSalario = new ButtonActionAlterarSalario();

		txCodigo = new JTextFieldId();
		pesquisaCliente = new CampoPesquisa<Cliente>(clienteService, PesquisaEnum.PES_CLIENTES.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Cliente objeto) {
				return String.format("%d - %s", objeto.getIdCliente(), objeto.getNome());
			}
		};

		dataAdmissao = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
		dataDemissao = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
		txSalario = new JMoneyField();
		txCargo = new JTextFieldMaiusculo();

		painelContent.setLayout(new MigLayout("", "[grow][grow][grow]", "[][][][][][][][][grow]"));

		painelContent.add(lblCodigo, "cell 0 0");
		painelContent.add(lblCliente, "cell 0 2");
		painelContent.add(lblCargo, "cell 0 4");

		painelContent.add(txCargo, "cell 0 5 3 1,growx");
		painelContent.add(lblAdmisso, "cell 0 6");
		painelContent.add(lblSalario, "cell 1 6");
		painelContent.add(dataDemissao, "cell 2 7,growx");
		painelContent.add(lblDemissao, "cell 2 6");
		painelContent.add(txCodigo, "cell 0 1,width 50:100:100");
		painelContent.add(pesquisaCliente, "cell 0 3 3 1,grow");
		painelContent.add(dataAdmissao, "cell 0 7,growx");
		painelContent.add(txSalario, "cell 1 7,growx");

		getContentPane().add(painelContent);

		Action actionAlterarSalario = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				new FrmAumentoSalarial(panelActions.getObjetoPesquisa()).setVisible(true);
			}

		};

		panelActions = new PanelActions<Funcionario>(this, funcionarioService,
				PesquisaEnum.PES_CLIENTES.getCodigoPesquisa(), alterarSalario) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void posicionarBotoes() {

				ContadorUtil contadorUtil = new ContadorUtil();

				posicionarBotao(contadorUtil, btPrimeiro, Boolean.TRUE);
				posicionarBotao(contadorUtil, btRetroceder, Boolean.TRUE);

				posicionarBotao(contadorUtil, alterarSalario, Boolean.TRUE);
				posicionarBotao(contadorUtil, btSalvar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btEditar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btNovo, Boolean.TRUE);
				posicionarBotao(contadorUtil, btBuscar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btCancelar, Boolean.TRUE);

				posicionarBotao(contadorUtil, btAvancar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btUltimo, Boolean.TRUE);

			}

			@Override
			protected void registrarEventosBotoesPagina() {
				registrarEvento(alterarSalario, actionAlterarSalario);
			}

			@Override
			public void carregarObjeto(Funcionario objeto) {

				txCodigo.setValue(objeto.getIdFuncionario());
				dataAdmissao.setDate(objeto.getDataAdmissao());
				dataDemissao.setDate(objeto.getDataDemissao());
				txSalario.setValue(objeto.getSalario());
				txCargo.setText(objeto.getCargo());
				pesquisaCliente.setValue(objeto.getCliente());
			}

			@Override
			public boolean preencherObjeto(Funcionario objetoPesquisa) {

				objetoPesquisa.setIdFuncionario(txCodigo.getValue());
				objetoPesquisa.setDataAdmissao(dataAdmissao.getDate());
				objetoPesquisa.setDataDemissao(dataDemissao.getDate());
				objetoPesquisa.setSalario(txSalario.getValue());
				objetoPesquisa.setCargo(txCargo.getText());
				objetoPesquisa.setCliente(pesquisaCliente.getObjetoPesquisado());

				return true;
			}

		};

		panelActions.addSaveListener(objeto -> txCodigo.setValue(objeto.getIdFuncionario()));
		panelActions.addButtonListener(new ButtonActionListenerAdapter() {

			@Override
			public void newEvent() {
				dataAdmissao.setDate(new Date());
			}

			@Override
			public void editEvent() {
				txSalario.setEditable(false);
			}
		});

		painelContent.add(panelActions, "cell 0 8 3 1,growx,aligny bottom");

	}

}
