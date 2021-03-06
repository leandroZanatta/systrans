package br.com.lar.ui;

import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.lar.repository.model.Motorista;
import br.com.lar.repository.model.Veiculo;
import br.com.lar.service.motorista.MotoristaService;
import br.com.lar.service.veiculo.VeiculoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.components.JNumericField;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.systrans.util.enumeradores.TipoVeiculoEnum;
import net.miginfocom.swing.MigLayout;

public class FrmVeiculo extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JTextFieldId txCodigo;
	private JLabel lblCodigo;
	private JLabel lbTipoVeiculo;
	private JLabel lbPlaca;
	private JTextFieldMaiusculo txPlaca;
	private PanelActions<Veiculo> panelActions;
	private MotoristaService motoristaService = new MotoristaService();
	private VeiculoService veiculoService = new VeiculoService();
	private JComboBox<TipoVeiculoEnum> cbTipoVeiculo;
	private JLabel lbCapacidade;
	private JMoneyField txCapacidade;
	private JLabel lblMotorista;
	private CampoPesquisa<Motorista> pesquisaMotorista;
	private JLabel lblAno;
	private JNumericField txAno;
	private JLabel lblMarca;
	private JTextFieldMaiusculo txMarca;
	private JTextFieldMaiusculo txModelo;
	private JLabel lblModelo;

	public FrmVeiculo(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(450, 260);
		setClosable(Boolean.TRUE);
		setTitle("CADASTRO DE VEÍCULOS");

		painelContent = new JPanel();
		txCodigo = new JTextFieldId();
		lblMarca = new JLabel("Marca:");
		lblModelo = new JLabel("Modelo:");
		lblMotorista = new JLabel("Motorista:");
		lbTipoVeiculo = new JLabel("Tipo de Veículo:");
		lblAno = new JLabel("Ano:");
		lblCodigo = new JLabel("Código:");
		cbTipoVeiculo = new JComboBox<>();
		txMarca = new JTextFieldMaiusculo();
		lbPlaca = new JLabel("Placa:");
		lbCapacidade = new JLabel("Capacidade:");
		txModelo = new JTextFieldMaiusculo();
		txPlaca = new JTextFieldMaiusculo();
		txAno = new JNumericField();
		txCapacidade = new JMoneyField();
		pesquisaMotorista = new CampoPesquisa<Motorista>(motoristaService, PesquisaEnum.PES_MOTORISTA.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Motorista objeto) {
				return String.format("%d - %s", objeto.getIdMotorista(), objeto.getFuncionario().getCliente().getNome());
			}
		};

		painelContent.setLayout(new MigLayout("", "[150.00][75.00][][grow]", "[][][][][][][][][grow]"));
		getContentPane().add(painelContent);
		painelContent.add(lblCodigo, "cell 0 0");
		painelContent.add(txCodigo, "cell 0 1,width 50:100:100");
		painelContent.add(lbTipoVeiculo, "cell 0 2");
		painelContent.add(lblMarca, "cell 2 2");
		painelContent.add(cbTipoVeiculo, "cell 0 3 2 1,growx");
		painelContent.add(txMarca, "cell 2 3 2 1,growx,aligny top");
		painelContent.add(lblModelo, "cell 0 4");
		painelContent.add(lbPlaca, "cell 1 4,growx");
		painelContent.add(lblAno, "cell 2 4");
		painelContent.add(lbCapacidade, "cell 3 4");
		painelContent.add(txModelo, "cell 0 5,growx,aligny top");
		painelContent.add(txPlaca, "cell 1 5,growx");
		painelContent.add(txAno, "cell 2 5,growx,aligny top");
		painelContent.add(txCapacidade, "cell 3 5,growx");
		painelContent.add(lblMotorista, "cell 0 6");
		painelContent.add(pesquisaMotorista, "cell 0 7 4 1,growx");

		Arrays.asList(TipoVeiculoEnum.values()).forEach(cbTipoVeiculo::addItem);

		panelActions = new PanelActions<Veiculo>(this, veiculoService, PesquisaEnum.PES_VEICULOS.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(Veiculo objeto) {
				txCodigo.setValue(objeto.getIdVeiculo());
				cbTipoVeiculo.setSelectedItem(TipoVeiculoEnum.forValue(objeto.getTipoVeiculo()));
				txPlaca.setText(objeto.getPlaca());
				txCapacidade.setValue(objeto.getCapacidade());
				txMarca.setText(objetoPesquisa.getMarca());
				txModelo.setText(objetoPesquisa.getModelo());
				txAno.setValue(objetoPesquisa.getNumeroAno());
				pesquisaMotorista.setValue(objeto.getMotorista());
			}

			@Override
			public boolean preencherObjeto(Veiculo objetoPesquisa) {

				objetoPesquisa.setIdVeiculo(txCodigo.getValue());
				objetoPesquisa.setTipoVeiculo(null);
				objetoPesquisa.setPlaca(txPlaca.getText());
				objetoPesquisa.setCapacidade(txCapacidade.getValue());
				objetoPesquisa.setMotorista(pesquisaMotorista.getObjetoPesquisado());
				objetoPesquisa.setMarca(txMarca.getText());
				objetoPesquisa.setModelo(txModelo.getText());
				objetoPesquisa.setNumeroAno(txAno.getValue());
				if (cbTipoVeiculo.getSelectedIndex() >= 0) {

					objetoPesquisa.setTipoVeiculo(((TipoVeiculoEnum) cbTipoVeiculo.getSelectedItem()).getCodigoVeiculo());
				}

				return true;
			}

		};

		panelActions.addSaveListener(objeto -> txCodigo.setValue(objeto.getIdVeiculo()));

		painelContent.add(panelActions, "cell 0 8 4 1,growx,aligny bottom");
	}

}
