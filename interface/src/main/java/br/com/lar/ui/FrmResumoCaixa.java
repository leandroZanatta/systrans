package br.com.lar.ui;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import net.miginfocom.swing.MigLayout;

public class FrmResumoCaixa extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JMoneyField textField;
	private JMoneyField textField_1;
	private JLabel lblTotalDeCrditos;
	private JLabel lblTotalDeDbitos;
	private JLabel lblSaldoMovimentado;
	private JLabel lblSaldoDeContas;
	private JLabel lblSobrafalta;
	private JMoneyField textField_2;
	private JMoneyField textField_3;
	private JMoneyField textField_4;
	private JMoneyField textField_5;
	private JMoneyField textField_6;
	private JPanel panel;
	private JButton btnFechar;

	public FrmResumoCaixa(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(370, 249);
		setClosable(Boolean.TRUE);
		setTitle("RESUMO DE CAIXA");

		painelContent = new JPanel();

		painelContent.setLayout(new MigLayout("", "[238.00,grow][grow]", "[][][][][][][][grow]"));
		getContentPane().add(painelContent);

		JLabel lblTotalMovimentado = new JLabel("Total Movimentado Crédito:");
		painelContent.add(lblTotalMovimentado, "cell 0 0,alignx left");

		textField = new JMoneyField();
		textField.setForeground(Color.BLUE);
		painelContent.add(textField, "cell 1 0,growx");

		JLabel lblTotalMovimentadoDebito = new JLabel("Total Movimentado Débito:");
		painelContent.add(lblTotalMovimentadoDebito, "cell 0 1,alignx left");

		textField_1 = new JMoneyField();
		textField_1.setForeground(Color.RED);
		painelContent.add(textField_1, "cell 1 1,growx");

		lblTotalDeCrditos = new JLabel("Total de Créditos:");
		painelContent.add(lblTotalDeCrditos, "cell 0 2,alignx left");

		textField_2 = new JMoneyField();
		textField_2.setForeground(Color.BLUE);
		painelContent.add(textField_2, "cell 1 2,growx");

		lblTotalDeDbitos = new JLabel("Total de Débitos:");
		painelContent.add(lblTotalDeDbitos, "cell 0 3,alignx left");

		textField_3 = new JMoneyField();
		textField_3.setForeground(Color.RED);
		painelContent.add(textField_3, "cell 1 3,growx");

		lblSaldoMovimentado = new JLabel("Saldo Movimentado:");
		painelContent.add(lblSaldoMovimentado, "cell 0 4,alignx left");

		textField_4 = new JMoneyField();
		painelContent.add(textField_4, "cell 1 4,growx");

		lblSaldoDeContas = new JLabel("Saldo de Contas:");
		painelContent.add(lblSaldoDeContas, "cell 0 5,alignx left");

		textField_5 = new JMoneyField();
		painelContent.add(textField_5, "cell 1 5,growx");

		lblSobrafalta = new JLabel("Sobra/Falta:");
		painelContent.add(lblSobrafalta, "cell 0 6,alignx left");

		textField_6 = new JMoneyField();
		painelContent.add(textField_6, "cell 1 6,growx");

		panel = new JPanel();
		painelContent.add(panel, "cell 0 7 2 1,growx,aligny bottom");

		btnFechar = new JButton("Fechar");
		btnFechar.addActionListener(e -> dispose());
		panel.add(btnFechar);

	}

}
