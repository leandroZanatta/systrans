package br.com.lar.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

import br.com.lar.repository.model.Funcionario;
import br.com.lar.repository.model.Motorista;
import br.com.lar.service.funcionario.FuncionarioService;
import br.com.lar.service.motorista.MotoristaService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JNumericField;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import net.miginfocom.swing.MigLayout;

public class FrmMotorista extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JLabel lblCodigo;
	private JLabel lbFuncionario;
	private JLabel lbVencimento;
	private JLabel lbNumeroDocumento;

	private JTextFieldId txCodigo;
	private CampoPesquisa<Funcionario> pesquisaFuncionario;
	private JDateChooser dtVencimento;

	private JPanel painelContent;
	private PanelActions<Motorista> panelActions;

	private FuncionarioService funcionarioService = new FuncionarioService();
	private MotoristaService motoristaService = new MotoristaService();
	private JTextFieldMaiusculo txCategoriaCNH;
	private JNumericField txNumeroDocumento;
	private JLabel lbCategoriaCNH;

	public FrmMotorista(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(500, 235);
		setClosable(Boolean.TRUE);
		setTitle("CADASTRO DE MOTORISTAS");

		painelContent = new JPanel();
		lblCodigo = new JLabel("Código:");
		lbVencimento = new JLabel("Data de Vencimento:");
		lbFuncionario = new JLabel("Funcionário:");
		lbNumeroDocumento = new JLabel("Número do Documento:");
		lbCategoriaCNH = new JLabel("Categoria CNH:");

		txCodigo = new JTextFieldId();
		txCategoriaCNH = new JTextFieldMaiusculo(5);
		txNumeroDocumento = new JNumericField();
		pesquisaFuncionario = new CampoPesquisa<Funcionario>(funcionarioService, PesquisaEnum.PES_FUNCIONARIOS.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Funcionario objeto) {
				return String.format("%d - %s", objeto.getIdFuncionario(), objeto.getCliente().getNome());
			}
		};
		dtVencimento = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');

		painelContent.setLayout(new MigLayout("", "[grow][grow][grow]", "[][][][30px:n][][][grow]"));

		painelContent.add(lblCodigo, "cell 0 0");
		painelContent.add(lbFuncionario, "cell 0 2");
		painelContent.add(lbNumeroDocumento, "cell 1 4");
		painelContent.add(txCategoriaCNH, "cell 0 5,growx");
		painelContent.add(txNumeroDocumento, "cell 1 5,growx");
		painelContent.add(dtVencimento, "cell 2 5,growx");
		painelContent.add(lbVencimento, "cell 2 4");
		painelContent.add(txCodigo, "cell 0 1,width 50:100:100");
		painelContent.add(pesquisaFuncionario, "cell 0 3 3 1,grow");
		painelContent.add(lbCategoriaCNH, "cell 0 4");

		getContentPane().add(painelContent);

		panelActions = new PanelActions<Motorista>(this, motoristaService,
				PesquisaEnum.PES_MOTORISTA.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(Motorista objeto) {

				txCodigo.setValue(objeto.getIdMotorista());
				pesquisaFuncionario.setValue(objeto.getFuncionario());
				dtVencimento.setDate(objeto.getDataVencimento());
				txCategoriaCNH.setText(objeto.getCnh());
				txNumeroDocumento.setValue(objeto.getNumeroDocumento());
			}

			@Override
			public boolean preencherObjeto(Motorista objetoPesquisa) {

				objetoPesquisa.setIdMotorista(txCodigo.getValue());
				objetoPesquisa.setFuncionario(pesquisaFuncionario.getObjetoPesquisado());
				objetoPesquisa.setDataVencimento(dtVencimento.getDate());
				objetoPesquisa.setCnh(txCategoriaCNH.getText());
				objetoPesquisa.setNumeroDocumento(txNumeroDocumento.getValue());

				return true;
			}

		};

		panelActions.addSaveListener(objeto -> txCodigo.setValue(objeto.getIdMotorista()));

		painelContent.add(panelActions, "cell 0 6 3 1,growx,aligny bottom");

	}

}
