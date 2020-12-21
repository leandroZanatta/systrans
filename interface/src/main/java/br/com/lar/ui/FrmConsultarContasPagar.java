package br.com.lar.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.toedter.calendar.JDateChooser;

import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.service.cliente.ClienteService;
import br.com.lar.service.contaspagar.ContasPagarService;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.tablemodels.ContasPagarTableModel;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.components.JNumericField;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.util.classes.SomaUtil;
import br.com.systrans.util.vo.PesquisaContasVO;
import net.miginfocom.swing.MigLayout;

public class FrmConsultarContasPagar extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;
	private ContasPagarService contasPagarService = new ContasPagarService();
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();
	private ClienteService clienteService = new ClienteService();
	private ContasPagarTableModel contasPagarTableModel = new ContasPagarTableModel();
	private JPanel container;
	private JLabel lblNewLabel;
	private JNumericField txCodigo;
	private CampoPesquisa<Cliente> pesquisaCliente;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	private JCheckBox chBaixado;
	private JDateChooser dtVencimentoInicial;
	private JDateChooser dtVencimentoFinal;
	private CampoPesquisa<FormasPagamento> pesquisaPagamento;
	private JMoneyField txValorInicial;
	private JMoneyField txValorFinal;
	private JTable table;
	private JLabel lbValorTotal;
	private JLabel lbDescontos;
	private JLabel lbAcrescimo;
	private JLabel lbTotalPago;
	private JLabel lbValorLiquido;
	private JTextFieldMaiusculo txDocumento;
	private JCheckBox chVencido;

	public FrmConsultarContasPagar(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		container = new JPanel();
		getContentPane().add(container, BorderLayout.CENTER);
		container.setLayout(null);

		lblNewLabel = new JLabel("Código:");
		lblNewLabel.setBounds(10, 10, 55, 14);
		container.add(lblNewLabel);

		txCodigo = new JNumericField();
		txCodigo.setBounds(77, 7, 86, 20);
		container.add(txCodigo);
		txCodigo.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Fornecedor:");
		lblNewLabel_1.setBounds(255, 10, 68, 14);
		container.add(lblNewLabel_1);

		pesquisaCliente = new CampoPesquisa<Cliente>(clienteService, PesquisaEnum.PES_CLIENTES.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Cliente objeto) {
				return String.format("%d - %s", objeto.getIdCliente(), objeto.getNome());
			}
		};
		pesquisaCliente.setBounds(322, 6, 455, 22);

		pesquisaPagamento = new CampoPesquisa<FormasPagamento>(formasPagamentoService, PesquisaEnum.PES_FORMAS_PAGAMENTO.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(FormasPagamento objeto) {
				return String.format("%d - %s", objeto.getIdFormaPagamento(), objeto.getDescricao());
			}
		};
		pesquisaPagamento.setBounds(322, 31, 455, 22);

		container.add(pesquisaCliente);

		JLabel lblNewLabel_5 = new JLabel("Documento:");
		lblNewLabel_5.setBounds(10, 35, 68, 14);
		container.add(lblNewLabel_5);

		JLabel lblNewLabel_4 = new JLabel("Pagamento:");
		lblNewLabel_4.setBounds(255, 34, 68, 14);
		container.add(lblNewLabel_4);

		container.add(pesquisaPagamento);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(7, 55, 280, 52);
		panel_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Vencimento", TitledBorder.CENTER, TitledBorder.TOP,
				null, new Color(0, 0, 0)));
		container.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblNewLabel_6 = new JLabel("De:");
		lblNewLabel_6.setBounds(10, 24, 23, 14);
		panel_1.add(lblNewLabel_6);

		dtVencimentoInicial = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
		dtVencimentoInicial.setBounds(38, 21, 100, 20);
		panel_1.add(dtVencimentoInicial);

		JLabel lblNewLabel_3 = new JLabel("Até:");
		lblNewLabel_3.setBounds(145, 24, 25, 14);
		panel_1.add(lblNewLabel_3);

		dtVencimentoFinal = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
		dtVencimentoFinal.setBounds(170, 21, 100, 20);
		panel_1.add(dtVencimentoFinal);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(285, 55, 235, 52);
		panel_2.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Pagamento", TitledBorder.CENTER, TitledBorder.TOP,
				null, new Color(0, 0, 0)));
		container.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblNewLabel_8 = new JLabel("De:");
		lblNewLabel_8.setBounds(10, 24, 22, 14);
		panel_2.add(lblNewLabel_8);

		txValorInicial = new JMoneyField();
		txValorInicial.setBounds(37, 21, 90, 20);
		txValorInicial.setColumns(8);
		panel_2.add(txValorInicial);

		JLabel lblNewLabel_9 = new JLabel("Até:");
		lblNewLabel_9.setBounds(130, 24, 25, 14);
		panel_2.add(lblNewLabel_9);

		txValorFinal = new JMoneyField();
		txValorFinal.setBounds(155, 21, 70, 20);
		txValorFinal.setColumns(8);
		panel_2.add(txValorFinal);

		chBaixado = new JCheckBox("Baixado");
		chBaixado.setBounds(525, 68, 76, 23);
		container.add(chBaixado);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(7, 111, 770, 203);
		container.add(scrollPane);

		table = new JTable(contasPagarTableModel);
		scrollPane.setViewportView(table);

		JPanel panel_3 = new JPanel();
		panel_3.setBounds(7, 318, 770, 54);
		panel_3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		container.add(panel_3);
		panel_3.setLayout(new MigLayout("", "[68px,grow][100px:n][100px:n][100px:n][100px:n][100px:n]", "[46px,grow]"));

		JLabel lblNewLabel_2 = new JLabel("Totalizadores:");
		panel_3.add(lblNewLabel_2, "cell 0 0,alignx right,aligny center");

		JPanel panel_7 = new JPanel();
		panel_7.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Valor Total", TitledBorder.CENTER, TitledBorder.TOP,
				null, null));
		panel_3.add(panel_7, "cell 1 0,grow");
		FlowLayout fl_panel_7 = new FlowLayout(FlowLayout.CENTER, 5, 0);
		panel_7.setLayout(fl_panel_7);

		lbValorTotal = new JLabel("0,00");
		panel_7.add(lbValorTotal);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Descontos", TitledBorder.CENTER, TitledBorder.TOP,
				null, new Color(0, 0, 0)));
		panel_3.add(panel_4, "cell 2 0,grow");
		panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

		lbDescontos = new JLabel("0,00");
		panel_4.add(lbDescontos);

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Acr\u00E9scimos", TitledBorder.CENTER,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_3.add(panel_5, "cell 3 0,grow");
		panel_5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

		lbAcrescimo = new JLabel("0,00");
		panel_5.add(lbAcrescimo);

		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Total Pago", TitledBorder.CENTER, TitledBorder.TOP,
				null, new Color(0, 0, 0)));
		panel_3.add(panel_6, "cell 4 0,grow");
		panel_6.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

		lbTotalPago = new JLabel("0,00");
		panel_6.add(lbTotalPago);

		JPanel panel_8 = new JPanel();
		panel_8.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Valor L\u00EDquido", TitledBorder.CENTER,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_3.add(panel_8, "cell 5 0,grow");
		panel_8.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

		lbValorLiquido = new JLabel("0");
		panel_8.add(lbValorLiquido);

		panel = new JPanel();
		panel.setBounds(7, 380, 770, 33);
		container.add(panel);

		btnNewButton_1 = new JButton("Baixar");
		btnNewButton_1.addActionListener((e) -> baixarContas());
		panel.add(btnNewButton_1);

		btnNewButton = new JButton("Cancelar");
		btnNewButton.addActionListener(e -> dispose());

		panel.add(btnNewButton);

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		txDocumento = new JTextFieldMaiusculo();
		txDocumento.setBounds(77, 32, 168, 20);
		container.add(txDocumento);
		txDocumento.setColumns(10);

		chVencido = new JCheckBox("Vencido");
		chVencido.setBounds(603, 68, 97, 23);
		container.add(chVencido);
		ListSelectionModel rowSelMod = table.getSelectionModel();

		rowSelMod.addListSelectionListener(e -> {

			if (!e.getValueIsAdjusting()) {
				ajustarPainelTotalizador();
			}
		});

		FocusAdapter focusAdapter = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				filtrarContasPagar();
			}
		};

		chBaixado.addActionListener(e -> filtrarContasPagar());
		chVencido.addActionListener(e -> filtrarContasPagar());
		txCodigo.addFocusListener(focusAdapter);
		dtVencimentoInicial.addPropertyChangeListener(e -> filtrarContasPagar());
		dtVencimentoFinal.addPropertyChangeListener(e -> filtrarContasPagar());
		txValorInicial.addFocusListener(focusAdapter);
		txDocumento.addFocusListener(focusAdapter);
		txValorFinal.addFocusListener(focusAdapter);
		pesquisaPagamento.addChangeListener(value -> filtrarContasPagar());
		pesquisaCliente.addChangeListener(value -> filtrarContasPagar());

		initComponents();

		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				filtrarContasPagar();
				super.internalFrameActivated(e);
			}
		});

	}

	private void baixarContas() {

		if (table.getSelectedRowCount() < 1) {

			JOptionPane.showMessageDialog(this, "Selecione ao menos uma conta á pagar");

			return;
		}

		List<ContasPagar> contasPagars = new ArrayList<>();

		int[] rows = table.getSelectedRows();

		for (int row : rows) {

			contasPagars.add(contasPagarTableModel.getRow(row));
		}

		if (contasPagars.stream().anyMatch(ContasPagar::isBaixado)) {

			JOptionPane.showMessageDialog(this, "NÃO É POSSÍVEL EFETUAR BAIXA DE CONTAS COM SITUAÇÃO BAIXADA. VERIFIQUE!");

			return;
		}

		FrmBaixarContasPagar frmBaixarContasPagar = new FrmBaixarContasPagar(getCodigoUsuario(), contasPagars);
		FrmApplication.getInstance().posicionarFrame(frmBaixarContasPagar, null);

	}

	private void filtrarContasPagar() {

		PesquisaContasVO pesquisaContasVO = new PesquisaContasVO();
		pesquisaContasVO.setCodigoConta(txCodigo.getValue());
		pesquisaContasVO.setCodigoCliente(getValueObject(pesquisaCliente.getObjetoPesquisado(), Cliente::getIdCliente));
		pesquisaContasVO
				.setCodigoFormaPagamento(getValueObject(pesquisaPagamento.getObjetoPesquisado(), FormasPagamento::getIdFormaPagamento));
		pesquisaContasVO.setDataVencimentoInicial(dtVencimentoInicial.getDate());
		pesquisaContasVO.setDataVencimentoFinal(dtVencimentoFinal.getDate());
		pesquisaContasVO.setDocumentoBaixado(chBaixado.isSelected());
		pesquisaContasVO.setValorParcelaInicial(txValorInicial.getValue());
		pesquisaContasVO.setValorParcelaFinal(txValorFinal.getValue());
		pesquisaContasVO.setCodigoDocumento(txDocumento.getText());
		pesquisaContasVO.setDocumentoVencido(chVencido.isSelected());

		List<ContasPagar> contasPagar = contasPagarService.filtrarContasPagar(pesquisaContasVO);

		contasPagarTableModel.setRows(contasPagar);

		ajustarPainelTotalizador();
	}

	private <K, T> T getValueObject(K objeto, Function<K, T> propertie) {

		if (objeto == null) {
			return null;
		}

		return propertie.apply(objeto);
	}

	private void ajustarPainelTotalizador() {

		List<ContasPagar> contasPagars = getContasSelecionadas();

		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setGroupingUsed(true);

		SomaUtil somaTotal = new SomaUtil();
		SomaUtil somaDescontos = new SomaUtil();
		SomaUtil somaAcrescimos = new SomaUtil();
		SomaUtil somaTotalPago = new SomaUtil();

		contasPagars.forEach(contaPagar -> {
			somaTotal.somar(contaPagar.getValorParcela());
			somaDescontos.somar(contaPagar.getValorDesconto());
			somaAcrescimos.somar(contaPagar.getValorAcrescimo());
			somaTotalPago.somar(contaPagar.getValorPago());
		});

		lbValorTotal.setText(numberFormat.format(somaTotal.getValue()));
		lbDescontos.setText(numberFormat.format(somaDescontos.getValue()));
		lbAcrescimo.setText(numberFormat.format(somaAcrescimos.getValue()));
		lbTotalPago.setText(numberFormat.format(somaTotalPago.getValue()));
		lbValorLiquido.setText(numberFormat
				.format(somaTotal.getValue().subtract(somaDescontos.getValue()).add(somaAcrescimos.getValue()).subtract(somaTotalPago.getValue())));
	}

	private List<ContasPagar> getContasSelecionadas() {

		if (table.getSelectedRowCount() <= 0) {

			return contasPagarTableModel.getRows();
		}

		List<ContasPagar> contasPagars = new ArrayList<>();

		for (int row : table.getSelectedRows()) {
			contasPagars.add(contasPagarTableModel.getRow(row));
		}

		return contasPagars;
	}

	private void initComponents() {

		setSize(800, 450);
		setClosable(Boolean.TRUE);
		setTitle("CONSULTAR CONTAS Á PAGAR");

		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setPreferredWidth(250);
		table.getColumnModel().getColumn(2).setPreferredWidth(90);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		table.getColumnModel().getColumn(4).setPreferredWidth(90);
		table.getColumnModel().getColumn(5).setPreferredWidth(90);
		table.getColumnModel().getColumn(6).setPreferredWidth(100);
		table.getColumnModel().getColumn(7).setPreferredWidth(60);
		table.getColumnModel().getColumn(8).setPreferredWidth(60);
	}
}
