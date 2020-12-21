package br.com.lar.ui.dialogs;

import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import br.com.lar.repository.model.FaturamentoEntradasDetalhe;
import br.com.lar.repository.model.Motorista;
import br.com.lar.repository.model.Veiculo;
import br.com.lar.service.motorista.MotoristaService;
import br.com.lar.service.veiculo.VeiculoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import net.miginfocom.swing.MigLayout;

public class FrmEntrada extends JDialog {

	private static final long serialVersionUID = 1L;
	private final FaturamentoEntradasDetalhe faturamentoEntradasDetalhe;
	private MotoristaService motoristaService = new MotoristaService();
	private VeiculoService veiculoService = new VeiculoService();
	private boolean ok = false;
	private JTextFieldId txCodigo;
	private JTextField txDocumento;
	private CampoPesquisa<Veiculo> pesquisaVeiculo;
	private CampoPesquisa<Motorista> pesquisaMotorista;
	private final Long codigoUsuario;
	private JPanel panel;
	private JButton btnSalvar;
	private JButton btnCancelar;
	private JLabel lblValorTotal;
	private JLabel lblAcrscimo;
	private JLabel lblDesconto;
	private JMoneyField txTotal;
	private JMoneyField txAcrescimo;
	private JMoneyField txDesconto;

	public FrmEntrada(FaturamentoEntradasDetalhe faturamentoEntradasDetalhe, Long codigoUsuario) {

		this.faturamentoEntradasDetalhe = faturamentoEntradasDetalhe;
		this.codigoUsuario = codigoUsuario;

		getContentPane().setLayout(new MigLayout("", "[grow][grow][grow][grow]", "[][][][][][][][][grow]"));

		JLabel lblCdigo = new JLabel("Código:");
		getContentPane().add(lblCdigo, "cell 0 0");

		txCodigo = new JTextFieldId();
		txCodigo.setEnabled(false);
		getContentPane().add(txCodigo, "cell 0 1,growx");

		JLabel lblVeiculo = new JLabel("Veiculo:");
		getContentPane().add(lblVeiculo, "cell 0 2");

		pesquisaVeiculo = new CampoPesquisa<Veiculo>(veiculoService, PesquisaEnum.PES_VEICULOS.getCodigoPesquisa(), codigoUsuario) {
			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Veiculo objeto) {

				return String.format("%d - %s", objeto.getIdVeiculo(), objeto.getPlaca());
			}
		};
		getContentPane().add(pesquisaVeiculo, "cell 0 3 4 1,grow");

		JLabel lblMotorista = new JLabel("Motorista:");
		getContentPane().add(lblMotorista, "cell 0 4");

		pesquisaMotorista = new CampoPesquisa<Motorista>(motoristaService, PesquisaEnum.PES_MOTORISTA.getCodigoPesquisa(), codigoUsuario) {
			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Motorista objeto) {

				return String.format("%d - %s", objeto.getIdMotorista(), objeto.getFuncionario().getCliente().getNome());
			}
		};
		getContentPane().add(pesquisaMotorista, "cell 0 5 4 1,grow");

		JLabel lblDocumento = new JLabel("Documento:");
		getContentPane().add(lblDocumento, "cell 0 6");

		lblValorTotal = new JLabel("Valor Total:");
		getContentPane().add(lblValorTotal, "cell 1 6");

		lblAcrscimo = new JLabel("Acréscimo");
		getContentPane().add(lblAcrscimo, "cell 2 6");

		lblDesconto = new JLabel("Desconto:");
		getContentPane().add(lblDesconto, "cell 3 6");

		txDocumento = new JTextField();
		getContentPane().add(txDocumento, "cell 0 7,growx");

		txTotal = new JMoneyField();
		getContentPane().add(txTotal, "cell 1 7,growx");

		txAcrescimo = new JMoneyField();
		getContentPane().add(txAcrescimo, "cell 2 7,growx");

		txDesconto = new JMoneyField();
		getContentPane().add(txDesconto, "cell 3 7,growx");

		panel = new JPanel();
		getContentPane().add(panel, "cell 0 8 4 1,growx,aligny bottom");

		btnSalvar = new JButton("Salvar");
		btnSalvar.addActionListener(e -> preencherDetalhe());
		panel.add(btnSalvar);

		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(e -> dispose());
		panel.add(btnCancelar);

		initComponents();

	}

	private void preencherDetalhe() {

		if (this.validarCampos()) {

			faturamentoEntradasDetalhe.setMotorista(pesquisaMotorista.getObjetoPesquisado());
			faturamentoEntradasDetalhe.setVeiculo(pesquisaVeiculo.getObjetoPesquisado());
			faturamentoEntradasDetalhe.setNumeroDocumento(txDocumento.getText());
			faturamentoEntradasDetalhe.setValorBruto(txTotal.getValue());
			faturamentoEntradasDetalhe.setValorAcrescimo(txAcrescimo.getValue());
			faturamentoEntradasDetalhe.setValorDesconto(txDesconto.getValue());

			ok = true;

			dispose();
		}
	}

	private boolean validarCampos() {

		if (!BigDecimalUtil.maior(txTotal.getValue(), BigDecimal.ZERO)) {

			JOptionPane.showMessageDialog(this, "O campo Valor Total deve ser maior que zero");

			return false;
		}

		return true;
	}

	private void initComponents() {

		setTitle("CADASTRO DE DESPESA");
		setSize(450, 270);
		setModal(true);
		setLocationRelativeTo(null);
	}

	public boolean isOk() {

		return this.ok;
	}

}
