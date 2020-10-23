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
		lblCodigo = new JLabel("Código:");
		lbTipoVeiculo = new JLabel("Tipo de Veículo:");
		lbPlaca = new JLabel("Placa:");
		txPlaca = new JTextFieldMaiusculo();
		txCapacidade = new JMoneyField();
		cbTipoVeiculo = new JComboBox<>();
		lbCapacidade = new JLabel("Capacidade:");
		lblMotorista = new JLabel("Motorista:");
		pesquisaMotorista = new CampoPesquisa<Motorista>(motoristaService, PesquisaEnum.PES_MOTORISTA.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Motorista objeto) {
				return String.format("%d - %s", objeto.getIdMotorista(), objeto.getFuncionario().getCliente().getNome());
			}
		};

		painelContent.setLayout(new MigLayout("", "[grow][grow]", "[][][][][][][][][grow]"));
		getContentPane().add(painelContent);
		painelContent.add(lblCodigo, "cell 0 0");
		painelContent.add(txCodigo, "cell 0 1,width 50:100:100");
		painelContent.add(txCapacidade, "cell 1 5,growx");
		painelContent.add(lbTipoVeiculo, "cell 0 2");
		painelContent.add(cbTipoVeiculo, "cell 0 3 2 1,growx");
		painelContent.add(lbPlaca, "cell 0 4,growx");
		painelContent.add(lbCapacidade, "cell 1 4");
		painelContent.add(txPlaca, "cell 0 5,growx");
		painelContent.add(lblMotorista, "cell 0 6");
		painelContent.add(pesquisaMotorista, "cell 0 7 2 1,growx");

		Arrays.asList(TipoVeiculoEnum.values()).forEach(cbTipoVeiculo::addItem);

		panelActions = new PanelActions<Veiculo>(this, veiculoService, PesquisaEnum.PES_VEICULOS.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(Veiculo objeto) {
				txCodigo.setValue(objeto.getIdVeiculo());
				cbTipoVeiculo.setSelectedItem(TipoVeiculoEnum.forValue(objeto.getTipoVeiculo()));
				txPlaca.setText(objeto.getPlaca());
				txCapacidade.setValue(objeto.getCapacidade());
				pesquisaMotorista.setValue(objeto.getMotorista());
			}

			@Override
			public boolean preencherObjeto(Veiculo objetoPesquisa) {

				objetoPesquisa.setIdVeiculo(txCodigo.getValue());
				objetoPesquisa.setTipoVeiculo(null);
				objetoPesquisa.setPlaca(txPlaca.getText());
				objetoPesquisa.setCapacidade(txCapacidade.getValue());
				objetoPesquisa.setMotorista(pesquisaMotorista.getObjetoPesquisado());

				if (cbTipoVeiculo.getSelectedIndex() >= 0) {

					objetoPesquisa.setTipoVeiculo(((TipoVeiculoEnum) cbTipoVeiculo.getSelectedItem()).getCodigoVeiculo());
				}

				return true;
			}

		};

		panelActions.addSaveListener(objeto -> txCodigo.setValue(objeto.getIdVeiculo()));

		painelContent.add(panelActions, "cell 0 8 2 1,growx,aligny bottom");
	}

}
