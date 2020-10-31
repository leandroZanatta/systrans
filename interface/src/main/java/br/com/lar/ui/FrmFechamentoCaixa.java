package br.com.lar.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import br.com.lar.repository.model.CaixaCabecalho;
import br.com.lar.repository.model.CaixaSaldo;
import br.com.lar.service.caixa.CaixaCabecalhoService;
import br.com.lar.tablemodels.DetalheFechamentoTableModel;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.vo.FechamentoCaixaVO;
import net.miginfocom.swing.MigLayout;

public class FrmFechamentoCaixa extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JScrollPane scrollPane;
	private JTable table;
	private JButton btnCancelar;
	private JButton btnFechar;
	private JLabel lblSaldoAtual;
	private JPanel panel;
	private JLabel lblPagamentos;
	private JLabel lblValorDinheiro;
	private JLabel lblCaixa;
	private JLabel lblNovoSaldo;
	private JTextField txDataAbertura;
	private JMoneyField txMovimentado;
	private JMoneyField txPagamentos;
	private JMoneyField txSaldoAtual;
	private JMoneyField txValorDinheiro;
	private JMoneyField txNovoSaldo;

	private CaixaCabecalhoService caixaCabecalhoService = new CaixaCabecalhoService();
	private DetalheFechamentoTableModel detalheFechamentoTableModel = new DetalheFechamentoTableModel();
	private CaixaCabecalho caixaCabecalho;

	public FrmFechamentoCaixa(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(450, 366);
		setClosable(Boolean.TRUE);
		setTitle("FECHAMENTO DE CAIXA");

		painelContent = new JPanel();

		painelContent.setLayout(new MigLayout("", "[308.00,grow][130.00px:n,grow]", "[][][][][][][][][][grow]"));
		getContentPane().add(painelContent);

		lblCaixa = new JLabel("Caixa:");
		JLabel lblDataDeAbertura = new JLabel("Data de Movimento:");
		JLabel lblValorEmDinheiro = new JLabel("Valor Movimentado:");

		btnFechar = new JButton("Fechar Caixa");
		btnFechar.addActionListener(e -> fecharCaixa());

		painelContent.add(lblCaixa, "cell 0 0 2 1,alignx center");
		painelContent.add(lblDataDeAbertura, "cell 0 1");
		txDataAbertura = new JTextField();
		txDataAbertura.setEditable(false);
		painelContent.add(txDataAbertura, "cell 1 1,growx");

		lblSaldoAtual = new JLabel("Saldo Atual:");
		painelContent.add(lblSaldoAtual, "cell 0 2,alignx left");

		txSaldoAtual = new JMoneyField();
		painelContent.add(txSaldoAtual, "cell 1 2,growx");
		painelContent.add(lblValorEmDinheiro, "cell 0 3");
		txMovimentado = new JMoneyField();
		painelContent.add(txMovimentado, "cell 1 3,growx");

		lblPagamentos = new JLabel("Pagamentos:");
		painelContent.add(lblPagamentos, "cell 0 4,alignx left");

		txPagamentos = new JMoneyField();
		painelContent.add(txPagamentos, "cell 1 4,growx");

		lblValorDinheiro = new JLabel("Valor Dinheiro:");
		painelContent.add(lblValorDinheiro, "cell 0 5,alignx left");

		txValorDinheiro = new JMoneyField();
		painelContent.add(txValorDinheiro, "cell 1 5,growx");

		lblNovoSaldo = new JLabel("Novo Saldo:");
		painelContent.add(lblNovoSaldo, "cell 0 6,alignx left");

		txNovoSaldo = new JMoneyField();
		painelContent.add(txNovoSaldo, "cell 1 6,growx");

		panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Detalhamento:",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		painelContent.add(panel, "cell 0 8 2 1,grow");
		panel.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);

		table = new JTable(detalheFechamentoTableModel);
		scrollPane.setViewportView(table);
		painelContent.add(btnFechar, "flowx,cell 0 9 2 1,alignx center,aligny bottom");

		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(e -> dispose());
		painelContent.add(btnCancelar, "cell 0 9 2 1");

		table.getColumnModel().getColumn(1).setMaxWidth(120);

		txMovimentado.setEditable(false);
		txPagamentos.setEditable(false);
		txSaldoAtual.setEditable(false);
		txValorDinheiro.setEditable(false);
		txNovoSaldo.setEditable(false);

		carregarCampos();
	}

	private void fecharCaixa() {

		try {

			caixaCabecalho.setDataFechamento(new Date());
			CaixaSaldo caixaSaldo = new CaixaSaldo();
			caixaSaldo.setDataMovimento(caixaCabecalho.getDataMovimento());
			caixaSaldo.setCaixaCabecalho(caixaCabecalho);
			caixaSaldo.setValorSaldo(txPagamentos.getValue().add(txValorDinheiro.getValue()));
			caixaSaldo.setValorSaldoAcumulado(txNovoSaldo.getValue());
			caixaCabecalho.setCaixaSaldo(caixaSaldo);

			caixaCabecalhoService.fecharCaixa(caixaCabecalho);

			JOptionPane.showMessageDialog(null, "Caixa fechado com sucesso");

			FrmApplication.getInstance().setarLabelCaixa();

			dispose();

		} catch (SysDescException e) {
			JOptionPane.showMessageDialog(this, e.getMensagem());
		}
	}

	private void carregarCampos() {

		try {
			caixaCabecalho = caixaCabecalhoService.validarExistenciaCaixaAberto(FrmApplication.getUsuario());

			FechamentoCaixaVO fechamentoCaixaVO = caixaCabecalhoService.buscarResumoFechamentoCaixa(caixaCabecalho);

			BigDecimal novoSaldo = fechamentoCaixaVO.getSaldoAtual().add(fechamentoCaixaVO.getValorPagamentos())
					.add(fechamentoCaixaVO.getValorDinheiro());

			lblCaixa.setText(caixaCabecalho.getCaixa().getDescricao());
			txDataAbertura.setText(new SimpleDateFormat("dd/MM/yyyy").format(caixaCabecalho.getDataMovimento()));
			detalheFechamentoTableModel.setRows(fechamentoCaixaVO.getDetalheFechamentoVOs());
			txSaldoAtual.setValue(fechamentoCaixaVO.getSaldoAtual());
			txMovimentado.setValue(fechamentoCaixaVO.getValorMovimentado());
			txPagamentos.setValue(fechamentoCaixaVO.getValorPagamentos());
			txValorDinheiro.setValue(fechamentoCaixaVO.getValorDinheiro());
			txNovoSaldo.setValue(novoSaldo);

			setarCorSaldo(txSaldoAtual);
			setarCorSaldo(txMovimentado);
			setarCorSaldo(txPagamentos);
			setarCorSaldo(txValorDinheiro);
			setarCorSaldo(txNovoSaldo);

		} catch (SysDescException e) {

			JOptionPane.showMessageDialog(this, e.getMensagem());

			btnFechar.setEnabled(false);
		}

	}

	private void setarCorSaldo(JMoneyField moneyField) {
		BigDecimal valor = moneyField.getValue();

		Color cor = Color.BLACK;

		if (BigDecimalUtil.maior(valor, BigDecimal.ZERO)) {
			cor = Color.BLUE;
		} else if (BigDecimalUtil.menor(valor, BigDecimal.ZERO)) {
			cor = Color.RED;
		}

		moneyField.setForeground(cor);
	}

}
